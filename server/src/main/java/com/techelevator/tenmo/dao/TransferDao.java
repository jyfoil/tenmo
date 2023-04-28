package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;

import java.util.List;

public interface TransferDao {

    public Transfer sendTransfer(Transfer transfer);
    public List<Transfer> getTransfersByUser(int userId);
    public Transfer getTransferById(int transferId);
    public List<Transfer> getPendingTransfersByAccount(int userId);
    public Transfer createTransfer(Transfer transfer);
    public boolean approveTransferRequest(Transfer transfer);
    public boolean rejectTransferRequest(int transferId);
    public TransferDTO mapTransferToTransferDTO(Transfer transfer);
    public Transfer mapTransferDTOToTransfer(TransferDTO transferDTO);
}
