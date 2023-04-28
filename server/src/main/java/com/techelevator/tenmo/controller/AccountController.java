package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import exceptions.AccountException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/user")
public class AccountController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    //README 4
    @RequestMapping(path = "/account/all", method = RequestMethod.GET)
    public List<Account> getAccounts(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        return accountDao.getAccounts(idFromUsername);
    }

    //README 3 and 16
    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public Account createAccount(Principal principal, @RequestBody Account account) {
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
