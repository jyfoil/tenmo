package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferDTO {

    @JsonProperty("user_sending")
    private String userSending;
    @JsonProperty("user_receiving")
    private String userReceiving;
    private BigDecimal amount;
    private String status;
    private int transferId;

    public TransferDTO() {

    }

    public TransferDTO(String userSending, String userReceiving, BigDecimal amount, String status, int transferId) {
        this.userSending = userSending;
        this.userReceiving = userReceiving;
        this.amount = amount;
        this.status = status;
        this.transferId = transferId;
    }

    public TransferDTO(String userSending, String userReceiving, BigDecimal amount, String status) {
        this.userSending = userSending;
        this.userReceiving = userReceiving;
        this.amount = amount;
        this.status = status;
    }

    public TransferDTO(String userSending, String userReceiving, BigDecimal amount) {
        this.userSending = userSending;
        this.userReceiving = userReceiving;
        this.amount = amount;
        this.status = "Pending";
    }

    public String getUserSending() {
        return userSending;
    }

    public void setUserSending(String userSending) {
        this.userSending = userSending;
    }

    public String getUserReceiving() {
        return userReceiving;
    }

    public void setUserReceiving(String userReceiving) {
        this.userReceiving = userReceiving;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
}
