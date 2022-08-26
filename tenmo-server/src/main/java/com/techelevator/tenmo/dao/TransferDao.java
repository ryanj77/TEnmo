package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    //blueprint for interacting with "transfer" table

    public void create(Transfer transfer);

    List<Transfer> getTransferLog(int userID);

    public int getAccountByUserID(int userID);

    public int getAccountByUserName(String username);

    public String getUserNameByAccountID(int accountID);

}
