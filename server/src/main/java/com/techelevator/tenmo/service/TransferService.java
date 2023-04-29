package com.techelevator.tenmo.service;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.UserInfoDTO;

import java.security.Principal;
import java.util.List;

public interface TransferService {
    List<UserInfoDTO> findAll();
    TransferDTO getTransferById(int id);
    List<TransferDTO> getTransfersByAccount(Principal principal);
    List<Transfer> getPendingTransfersByAccount(Principal principal);
    TransferDTO sendTransfer(Principal principal, TransferDTO sendDTO);
    TransferDTO requestTransfer(Principal principal, TransferDTO requestDTO);
    TransferDTO approveTransfer(Principal principal, int id);
    String rejectTransfer(Principal principal, int id);
}