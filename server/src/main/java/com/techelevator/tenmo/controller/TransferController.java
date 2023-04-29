package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserInfoDTO;
import com.techelevator.tenmo.service.TransferService;
import exceptions.DaoException;
import exceptions.TransferException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/user")
public class TransferController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;
    private TransferService transferService;

    public TransferController(AccountDao accountDao, TransferDao transferDao,
                              UserDao userDao, TransferService transferService) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.transferService = transferService;
    }

    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public List<UserInfoDTO> findAll() {
        return transferService.findAll();
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public TransferDTO getTransferById(@PathVariable int id) {
        return transferService.getTransferById(id);
    }

    @RequestMapping(path = "/transfer/all", method = RequestMethod.GET)
    public List<TransferDTO> getTransfersByAccount(Principal principal) {
        return transferService.getTransfersByAccount(principal);
    }

    @RequestMapping(path = "/transfer/pending", method = RequestMethod.GET)
    public List<TransferDTO> getPendingTransfersByAccount(Principal principal) {
        return transferService.getPendingTransfersByAccount(principal);
    }

    @RequestMapping(path = "/transfer/send", method = RequestMethod.POST)
    public TransferDTO sendTransfer(Principal principal, @RequestBody TransferDTO sendDTO) {
        return transferService.sendTransfer(principal, sendDTO);
    }

    @RequestMapping(path = "/transfer/request", method = RequestMethod.POST)
    public TransferDTO requestTransfer(Principal principal, @RequestBody TransferDTO requestDTO) {
        return transferService.requestTransfer(principal, requestDTO);
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.PUT)
    public TransferDTO approveTransfer(Principal principal, @PathVariable int id) {
        return transferService.approveTransfer(principal, id);
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.DELETE)
    public String rejectTransfer(Principal principal, @PathVariable int id) {
        return transferService.rejectTransfer(principal, id);
    }
}
