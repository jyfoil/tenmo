package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import exceptions.AccountException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;

@Component
public class AccountServiceLogic implements AccountService {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public AccountServiceLogic(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    public List<Account> getAccounts(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        return accountDao.getAccounts(idFromUsername);
    }

    public Account createAccount(Principal principal, Account account) {
        // We want to get the username that's associated with this account
        if (!principal.getName().equals(userDao.findUsernameById(account.getUserId()))) {
            throw new AccountException("Cannot create account for a different user");
        }
        if (account.isPrimaryAccount()){
            throw new AccountException("Cannot create a second primary account");
        }
        return accountDao.createAccount(account);
    }
}
