package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
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

//    Creates and completes a transfer as the sender
    @Override
    public Transfer sendTransfer(Transfer transfer) {
       Transfer sentTransfer = createTransfer(transfer);
        if (approveTransferRequest(sentTransfer)) {
            updateStatus(sentTransfer);
            return sentTransfer;
        } else {
            throw new DaoException("Transfer not completed");
        }
    }

    //gets all accounts the logged in user has and their balances
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
    public List<Transfer> getPendingTransfersByAccount(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, account_id_send, account_id_receive, amount, pending FROM transfer " +
                "WHERE (pending = true AND (account_id_send = (SELECT account_id FROM account WHERE user_id = ?))) " +
                "OR (pending = true AND (account_id_receive = (SELECT account_id FROM account WHERE user_id = ?)));";
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
        String sql = "BEGIN TRANSACTION; UPDATE account SET balance = balance - ? WHERE account_id = ?; " +
                "UPDATE account SET balance = balance + ? WHERE account_id = ?; COMMIT;";
        try {
            jdbcTemplate.update(sql, transfer.getAmount(), transfer.getAccountIdSending(),
                    transfer.getAmount(), transfer.getAccountIdReceiving());
            approved = true;
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

    @Override
    public TransferDTO mapTransferToTransferDTO(Transfer transfer) {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(transfer.getAmount());
        transferDTO.setUserSending(userDao.getUsernameByAccountId(transfer.getAccountIdSending()));
        transferDTO.setUserReceiving(userDao.getUsernameByAccountId(transfer.getAccountIdReceiving()));
        transferDTO.setStatus(transfer.isPending() ? "Pending" : "Approved");
        return transferDTO;
    }

    @Override
    public Transfer mapTransferDTOToTransfer(TransferDTO transferDTO) {
        Transfer transfer = new Transfer();
        transfer.setAccountIdReceiving(getPrimaryAccountIDFromUsername(transferDTO.getUserReceiving()));
        transfer.setAccountIdSending(getPrimaryAccountIDFromUsername(transferDTO.getUserSending()));
        transfer.setAmount(transferDTO.getAmount());
        transfer.setPending(true);
        return transfer;
    }

    public int getPrimaryAccountIDFromUsername(String username){
        int accountId = 0;
        String sql = "SELECT account_id FROM account WHERE primary_account = true AND user_id = ?";
        try {
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userDao.findIdByUsername(username));
        if (result.next()){
            accountId = result.getInt("account_id");
        }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        }
        return accountId;
    }

    private boolean updateStatus(Transfer transfer){
        boolean updated = false;
        String sql = "UPDATE transfer SET pending = false WHERE transfer_id = ?";
        try {
            int numberOfRows = jdbcTemplate.update(sql, transfer.getTransferId());
            if (numberOfRows > 0){
                updated = true;
            } else {
                throw new DaoException("Transfer status not updated");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity violation", e);
        }
        return updated;
    }
}
