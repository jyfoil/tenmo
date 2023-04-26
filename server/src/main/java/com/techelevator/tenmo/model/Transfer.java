package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private int accountIdSending;
    private int accountIdReceiving;
    private BigDecimal amount;
    private boolean pendingStatus;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getAccountIdSending() {
        return accountIdSending;
    }

    public void setAccountIdSending(int accountIdSending) {
        this.accountIdSending = accountIdSending;
    }

    public int getAccountIdReceiving() {
        return accountIdReceiving;
    }

    public void setAccountIdReceiving(int accountIdReceiving) {
        this.accountIdReceiving = accountIdReceiving;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isPendingStatus() {
        return pendingStatus;
    }

    public void setPendingStatus(boolean pendingStatus) {
        this.pendingStatus = pendingStatus;
    }

    public String toString() {
        return "";
    }
}
