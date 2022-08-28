package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;

import java.util.List;

public interface TransferDao {
    //blueprint for interacting with "transfer" table

    public void create(Transfer transfer);

    List<Transfer> getTransferLog(int accountID);

    public int getAccountByUserID(int userID);

    public int getAccountByUserName(String username);

    public String getUserNameByAccountID(int accountID);

    TransferStatus getTransferStatus(String status);

    TransferType getTransferType(String type);
}
