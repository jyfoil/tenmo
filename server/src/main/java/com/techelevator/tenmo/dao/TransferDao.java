package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public Transfer completeTransfer(Transfer transfer);
    public List<Transfer> getTransfersByAccount(int userId);
    public Transfer getTransferById(int transferId);
    public List<Transfer> getPendingTransfersByAccount(int userId);
    public Transfer createTransfer(Transfer transfer);
    public boolean approveTransferRequest(Transfer transfer);
    public boolean rejectTransferRequest(int transferId);
}
