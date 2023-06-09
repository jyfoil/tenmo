package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import exceptions.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Account> getAccounts(int id) {
        List<Account> accounts = new ArrayList<>();

        String sql = "SELECT account_id, user_id, balance, primary_account " +
                     "FROM account " +
                     "WHERE user_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            while (results.next()) {
                accounts.add(mapRowToAccount(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        }

        return accounts;
    }

    @Override
    public Account getAccount(int userId, int accountId) {
        Account account = null;

        String sql = "SELECT account_id, user_id, balance, primary_account " +
                     "FROM account " +
                     "WHERE user_id = ? AND account_id = ?;";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql,userId, accountId);

            if (result.next()) {
                account = mapRowToAccount(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        }


        return account;
    }

    @Override
    public Account createAccount(Account account) {
        Account createdAccount = null;

        String sql = "INSERT into account (user_id, balance, primary_account) " +
                     "VALUES (?, ?, ?) RETURNING account_id;";

        int newAccountId = 0;
        
        try {
            newAccountId = jdbcTemplate.queryForObject(sql, int.class, account.getUserId(), account.getBalance(), account.isPrimaryAccount());

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to database", e);
        } catch (BadSqlGrammarException e) {
            throw new DaoException("Sql Syntax error", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity violation", e);
        }

        createdAccount = getAccount(account.getUserId(), newAccountId);


        return createdAccount;
    }

    public BigDecimal getPrimaryAccountBalanceByUsername(String username){
        BigDecimal balance = null;
        String sql = "SELECT balance FROM account WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?) " +
                "AND primary_account = true";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if (result.next()){
            balance = result.getBigDecimal("balance");
        }
        return balance;
    }


    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        account.setPrimaryAccount(rowSet.getBoolean("primary_account"));
        return account;
    }
}
