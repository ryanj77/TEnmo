package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    //model class for transfer object attributes
    private int fromAccountID;
    private int transferTypeID; //on transfer table, 1=send, 2=req
    private int transferStatusID; //on transfer table, 1=pending, 2=approved, 3=rejected
    private int toAccountID;
    private int transferID;
    BigDecimal transferAmt;

    public Transfer(int fromAccountID, int transferTypeID, int transferStatusID, int toAccountID, int transferID, BigDecimal transferAmt) {
        this.fromAccountID = fromAccountID;
        this.transferTypeID = transferTypeID;
        this.transferStatusID = transferStatusID;
        this.toAccountID = toAccountID;
        this.transferID = transferID;
        this.transferAmt = transferAmt;
    }
    public Transfer() { //default constructor
    }

    public int getFromAccountID() {
        return fromAccountID;
    }

    public void setFromAccountID(int fromAccountID) {
        this.fromAccountID = fromAccountID;
    }

    public int getToAccountID() {
        return toAccountID;
    }

    public void setToAccountID(int toAccountID) {
        this.toAccountID = toAccountID;
    }

    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public BigDecimal getTransferAmt() {
        return transferAmt;
    }

    public void setTransferAmt(BigDecimal transferAmt) {
        this.transferAmt = transferAmt;
    }

    public int getTransferTypeID() {
        return transferTypeID;
    }

    public void setTransferTypeID(int transferTypeID) {
        this.transferTypeID = transferTypeID;
    }

    public int getTransferStatusID() {
        return transferStatusID;
    }

    public void setTransferStatusID(int transferStatusID) {
        this.transferStatusID = transferStatusID;
    }

    }







