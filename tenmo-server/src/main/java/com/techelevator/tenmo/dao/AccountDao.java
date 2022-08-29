package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.InsufficientFundsException;
import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

// blueprint for interacting with "account" table
public interface AccountDao {
    Account findByUsername(String username);
    Account findByUserId(int id);
    Account findUsernameByAccountID(int accountID);
    Account findByAccountId(int id);
    void transferMoney(int fromAccountId, int toAccountId, BigDecimal amount) throws InsufficientFundsException;

}