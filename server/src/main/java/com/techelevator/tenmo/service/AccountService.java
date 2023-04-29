package com.techelevator.tenmo.service;

import com.techelevator.tenmo.model.Account;

import java.security.Principal;
import java.util.List;

public interface AccountService {
    List<Account> getAccounts(Principal principal);
    Account createAccount(Principal principal, Account account);
}
