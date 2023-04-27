package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferRequestDTO;
import com.techelevator.tenmo.model.User;
import exceptions.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;

    public JdbcTransferDao(DataSource dataSource, UserDao userDao) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.userDao = userDao;
    }

    //Creates and completes a transfer as the sender
    @Override
    public Transfer sendTransfer(Transfer transfer) {
       Transfer sentTransfer = createTransfer(transfer);
        if (approveTransferRequest(sentTransfer)) {
            return sentTransfer;
        } else {
            throw new DaoException("Transfer not completed");
        }
    }

    //gets all accounts the logged in user has and their balances
    //TODO: change method so that usernames display instead of account ids
    @Override
    public List<Transfer> getTransfersByUser(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, account_id_send, account_id_receive, amount, pending FROM transfer " +
                "WHERE account_id_send IN (SELECT account_id FROM account WHERE user_id = ?) " +
                "OR account_id_receive IN (SELECT account_id FROM account WHERE user_id = ?); ";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        }
        return transfers;
    }

    //returns a specific transfer by its id
    @Override
    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, account_id_send, account_id_receive, amount, pending " +
                "FROM transfer WHERE transfer_id = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()) {
                transfer = (mapRowToTransfer(result));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        }
        return transfer;
    }

    //gets all pending transfers that a logged in user has
    @Override
    public List<Transfer> getPendingTransfersByAccount(int id, boolean pending) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, account_id_send, account_id_receive, amount, pending FROM transfer " +
                "WHERE account_id_send = (SELECT account_id FROM account WHERE user_id = ?) AND pending = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, pending);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        }
        return transfers;
    }

    //initiates a transfer from the recipient/requester
    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer requestedTransfer = null;
        String sql = "INSERT INTO transfer (account_id_send, account_id_receive, amount, pending) " +
                    "VALUES (?, ?, ?, ?) RETURNING transfer_id";
        try {
            Integer newTransferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getAccountIdSending(), transfer.getAccountIdReceiving(),
                    transfer.getAmount(), transfer.isPending());
            requestedTransfer = getTransferById(newTransferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
         } catch (DataIntegrityViolationException e) {
         throw new DaoException("Data Integrity violation", e);
        }
        return requestedTransfer;
    }

    //approves a transfer as the sender and carries out transaction
    @Override
    public boolean approveTransferRequest(Transfer transfer) {
        boolean approved = false;
        String sql = "BEGIN TRANSACTION; UPDATE account SET balance = balance - ? WHERE account_id = ? " +
                "UPDATE account SET balance = balance + ? WHERE account_id = ? COMMIT;";
        try {
            int numberOfRows = jdbcTemplate.update(sql, transfer.getAmount(), transfer.getAccountIdSending(),
                    transfer.getAmount(), transfer.getAccountIdReceiving());
            if (numberOfRows != 2){
                throw new DaoException("Expected exactly 2 rows");
            } else {
                transfer.setPending(false);
                approved = true;
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity violation", e);
        }
        return approved;
    }

    //rejects a transfer as the sender and deletes transaction from records
    @Override
    public boolean rejectTransferRequest(int id) {
        boolean deleted = false;
        String sql = "DELETE FROM transfer WHERE transfer_id = ?";
        try {
            int numberOfDeletedRows = jdbcTemplate.update(sql, id);
            if (numberOfDeletedRows != 0){
                deleted = true;
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity violation", e);
        }
        return deleted;
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet){
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setAccountIdSending(rowSet.getInt("account_id_send"));
        transfer.setAccountIdReceiving(rowSet.getInt("account_id_receive"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        transfer.setPending(rowSet.getBoolean("pending"));
        return transfer;
    }

    public TransferRequestDTO mapTransferToTransferDTO(Transfer transfer) {
        TransferRequestDTO transferDTO = new TransferRequestDTO();
        transferDTO.setAmount(transfer.getAmount());
        transferDTO.setUserSending(userDao.getUsernameByAccountId(transfer.getAccountIdSending()));
        transferDTO.setUserReceiving(userDao.getUsernameByAccountId(transfer.getAccountIdReceiving()));
        return transferDTO;
    }
}
