package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
//@PreAuthorize("isAuthenticated()")
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

    @RequestMapping(path = "/account/all", method = RequestMethod.GET)
    public List<Account> getAccounts(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        return accountDao.getAccounts(idFromUsername);
    }

    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public List<User> findAll(){
        return userDao.findAll();
    }

    //todo: this method does not function as designed, needs a fix
    @RequestMapping(path = "/account/transfer/send", method = RequestMethod.POST)
    public Transfer sendTransfer(Principal principal, @RequestBody Transfer transfer){
        return transferDao.sendTransfer(transfer);
    }
//
//    @RequestMapping(path = "/{username}", method = RequestMethod.POST)
//    public Account createAccount(@PathVariable String username,
//                                 @RequestBody Account account) {
//        return accountDao.createAccount(account);
//    }

    @RequestMapping(path = "/transfer/all", method = RequestMethod.GET)
    public List<Transfer> getTransfersByAccount(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        return transferDao.getTransfersByAccount(idFromUsername);
    }

//    @RequestMapping(path = "/transfer/{username}/{id}", method = RequestMethod.GET)
//    public Transfer getTransferById(@PathVariable String username, @PathVariable int id) {
//        return transferDao.getTransferById(id);
//    }

    //TODO: replace working method paths with endpoints that don't include user info

    @RequestMapping(path = "/transfer/{username}/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfersByAccount(@PathVariable String username) {
        int idFromUsername = userDao.findIdByUsername(username);
        return transferDao.getPendingTransfersByAccount(idFromUsername, true);
    }

    //TODO: replace method above with one in which requestparam functions
}
