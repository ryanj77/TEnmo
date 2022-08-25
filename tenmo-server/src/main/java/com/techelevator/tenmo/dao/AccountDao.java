package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;

// blueprint for interacting with "account" table
public interface AccountDao {
    Account findByUsername(String username) throws AccountNotFoundException;
}