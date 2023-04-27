package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferRequestDTO {

    private String userSending;
    private String userReceiving;
    private BigDecimal amount;

    public TransferRequestDTO() {

    }

    public TransferRequestDTO(String userSending, String userReceiving, BigDecimal amount) {
        this.userSending = userSending;
        this.userReceiving = userReceiving;
        this.amount = amount;
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
}
