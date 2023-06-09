package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Account {

    @JsonProperty("account_id")
    private int accountId;
    @JsonProperty("user_id")
    private int userId;
    private BigDecimal balance;
    @JsonProperty("primary_account")
    private boolean primaryAccount;

    public Account (){

    }

    public Account (int id, BigDecimal balance, boolean primaryAccount) {
        this.userId = id;
        this.balance = balance;
        this.primaryAccount = primaryAccount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String toString() {
        return "Account has " + balance;
    }

    public boolean isPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(boolean primaryAccount) {
        this.primaryAccount = primaryAccount;
    }
}
