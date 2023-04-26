package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public Transfer sendTransfer();
    public List<Transfer> getTransfers();
    public List<Transfer> getPendingTransfers();
    public Transfer requestTransfer();
    public Transfer approveTransfer();
    public boolean rejectTransfer();
}
