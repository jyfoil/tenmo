package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.service.AccountService;
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
    private AccountService accountService;

    public AccountController(AccountDao accountDao, TransferDao transferDao, UserDao userDao,
                             AccountService accountService) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountService = accountService;
    }

    @RequestMapping(path = "/account/all", method = RequestMethod.GET)
    public List<Account> getAccounts(Principal principal) {
        return accountService.getAccounts(principal);
    }

    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public Account createAccount(Principal principal, @RequestBody Account account) {
        return accountService.createAccount(principal, account);
    }
}
