package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public Transfer sendTransfer(Transfer transfer);
    public List<Transfer> getTransfersByAccount(int userId);
    public Transfer getTransferById(int transferId);
    public List<Transfer> getPendingTransfersByAccount(int userId, boolean pending);
    public Transfer createTransfer(Transfer transfer);
    public boolean approveTransferRequest(Transfer transfer);
    public boolean rejectTransferRequest(int transferId);
}
