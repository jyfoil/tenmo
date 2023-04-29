package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    public List<Account> getAccounts(int userId);
    public Account getAccount(int userId, int accountId);
    public Account createAccount(Account account);
    BigDecimal getPrimaryAccountBalanceByUsername(String username);
}
