package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserInfoDTO;
import exceptions.DaoException;
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

    public TransferController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    //README 5.i and 8.i
    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public List<UserInfoDTO> findAll(){
        List<UserInfoDTO> userinfo = new ArrayList<>();
        List<User> users = userDao.findAll();
        for (User eachUser : users){
            userinfo.add(userDao.mapUserToUserInfoDTO(eachUser));
        }
        return userinfo;
    }

    //README 7
    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public TransferDTO getTransferById(@PathVariable int id) {
        Transfer transfer = transferDao.getTransferById(id);
        TransferDTO transferRequest = new TransferDTO();
        transferRequest = transferDao.mapTransferToTransferDTO(transfer);
        return transferRequest;
    }

    //README 6
    @RequestMapping(path = "/transfer/all", method = RequestMethod.GET)
    public List<TransferDTO> getTransfersByAccount(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        List<Transfer> transfers = transferDao.getTransfersByUser(idFromUsername);
        List<TransferDTO> transferRequests = new ArrayList<>();
        for (Transfer eachTransfer : transfers) {
            transferRequests.add(transferDao.mapTransferToTransferDTO(eachTransfer));
        }
        return transferRequests;
    }

    //README 9
    @RequestMapping(path = "/transfer/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfersByAccount(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        return transferDao.getPendingTransfersByAccount(idFromUsername);
    }

    //README 5
    @RequestMapping(path = "/transfer/send", method = RequestMethod.POST)
    public Transfer sendTransfer(Principal principal, @RequestBody TransferDTO sendDTO){
        if (!sendDTO.getUserSending().equals(principal.getName())){
           throw new DaoException("Hey, you can't send money on behalf of someone else!");
        }
        Transfer transfer = transferDao.mapTransferDTOToTransfer(sendDTO);
        return transferDao.sendTransfer(transfer);
    }

    //README 8
    @RequestMapping(path = "/transfer/request", method = RequestMethod.POST)
    public Transfer requestTransfer(Principal principal, @RequestBody TransferDTO requestDTO){
        if (!requestDTO.getUserReceiving().equals(principal.getName())){
            throw new DaoException("Hey, you can't do that!");
        }
        Transfer transfer = transferDao.mapTransferDTOToTransfer(requestDTO);
        return transferDao.createTransfer(transfer);
    }
}
