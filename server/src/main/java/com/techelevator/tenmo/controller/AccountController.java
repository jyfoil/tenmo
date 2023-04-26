package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
//@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/account")
public class AccountController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public AccountController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/{username}", method = RequestMethod.GET)
    public List<Account> getAccounts(@PathVariable String username) {
        int idFromUsername = userDao.findIdByUsername(username);
        return accountDao.getAccounts(idFromUsername);
    }

    @RequestMapping(path = "/{username}", method = RequestMethod.POST)
    public Account createAccount(@PathVariable String username,
                                 @RequestBody Account account) {
        return accountDao.createAccount(account);
    }

    @RequestMapping(path = "/transfer/{username}", method = RequestMethod.GET)
    public List<Transfer> getTransfersByAccount(@PathVariable String username) {
        int idFromUsername = userDao.findIdByUsername(username);
        return transferDao.getTransfersByAccount(idFromUsername);
    }
}
