package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserInfoDTO;
import exceptions.TransferException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransferServiceLogic implements TransferService {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public TransferServiceLogic(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @Override
    public List<UserInfoDTO> findAll() {
        List<UserInfoDTO> userinfo = new ArrayList<>();
        List<User> users = userDao.findAll();
        for (User eachUser : users) {
            userinfo.add(userDao.mapUserToUserInfoDTO(eachUser));
        }
        return userinfo;
    }

    @Override
    public TransferDTO getTransferById(int id) {
        Transfer transfer = transferDao.getTransferById(id);
        TransferDTO transferRequest = new TransferDTO();
        transferRequest = transferDao.mapTransferToTransferDTO(transfer);
        return transferRequest;
    }

    @Override
    public List<TransferDTO> getTransfersByAccount(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        List<Transfer> transfers = transferDao.getTransfersByUser(idFromUsername);
        List<TransferDTO> transferRequests = new ArrayList<>();
        for (Transfer eachTransfer : transfers) {
            transferRequests.add(transferDao.mapTransferToTransferDTO(eachTransfer));
        }
        return transferRequests;
    }

    @Override
    public List<TransferDTO> getPendingTransfersByAccount(Principal principal) {
        int idFromUsername = userDao.findIdByUsername(principal.getName());
        List<Transfer> transfers = transferDao.getPendingTransfersByAccount(idFromUsername);
        List<TransferDTO> transferDTOs = new ArrayList<>();
        for (Transfer eachTransfer : transfers){
            transferDTOs.add(transferDao.mapTransferToTransferDTO(eachTransfer));
        }
        return transferDTOs;
    }

    @Override
    public TransferDTO sendTransfer(Principal principal, TransferDTO sendDTO) {
        if (sendDTO.getUserSending().equals(sendDTO.getUserReceiving())){
            throw new TransferException("Cannot send to the same user");
        }
        BigDecimal amountInSenderAccount = accountDao.getPrimaryAccountBalanceByUsername(sendDTO.getUserSending());
        if (sendDTO.getAmount().compareTo(amountInSenderAccount) == 1){
            throw new TransferException("Cannot send more money than primary account balance");
        }
        if (sendDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new TransferException("Cannot send amounts that are zero or lower");
        }
        if (!sendDTO.getUserSending().equals(principal.getName())) {
            throw new TransferException("Hey, you can't send money on behalf of someone else!");
        }
        Transfer transfer = transferDao.mapTransferDTOToTransfer(sendDTO);
        Transfer sentTransfer = transferDao.sendTransfer(transfer);
        return transferDao.mapTransferToTransferDTO(sentTransfer);
    }

    @Override
    public TransferDTO requestTransfer(Principal principal, TransferDTO requestDTO) {
        if (requestDTO.getUserReceiving().equals(requestDTO.getUserSending())){
            throw new TransferException("Cannot request money from own account");
        }
        if (requestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new TransferException("Cannot request amounts that are zero or lower");
        }
        if (!requestDTO.getUserReceiving().equals(principal.getName())) {
            throw new TransferException("Hey, you can't do that!");
        }
        Transfer transfer = transferDao.mapTransferDTOToTransfer(requestDTO);
        Transfer requestTransfer = transferDao.createTransfer(transfer);
        return transferDao.mapTransferToTransferDTO(requestTransfer);
    }

    @Override
    public TransferDTO approveTransfer(Principal principal, int id) {
        BigDecimal amountInSenderAccount = accountDao.getPrimaryAccountBalanceByUsername(principal.getName());
        if (transferDao.getTransferById(id).getAmount().compareTo(amountInSenderAccount) == 1){
            throw new TransferException("Cannot approve transfer larger than primary account balance");
        }
        if (!getTransferById(id).getUserSending().equals(principal.getName())) {
            throw new TransferException("Cannot approve transfer on behalf of another user");
        }
        Transfer transfer = transferDao.mapTransferDTOToTransfer(getTransferById(id), id);
        if (!transferDao.approveTransferRequest(transfer)) {
            throw new TransferException("Transfer was not approved");
        }
        transferDao.updateStatus(transfer);
        return transferDao.mapTransferToTransferDTO(transfer);
    }

    //TODO: Find a way to show the user their transfer id so they know what to delete/approve
    public String rejectTransfer(Principal principal, int id) {
        Transfer transfer = transferDao.getTransferById(id);
        if (principal.getName().equals(userDao.getUsernameByAccountId(transfer.getAccountIdSending()))
            || principal.getName().equals(userDao.getUsernameByAccountId(transfer.getAccountIdReceiving()))) {
            return transferDao.rejectTransferRequest(id);
        }
        throw new TransferException("Cannot delete transfer for another user");
    }
}
