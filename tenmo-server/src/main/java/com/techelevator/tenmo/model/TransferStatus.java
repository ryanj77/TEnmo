package com.techelevator.tenmo.model;

public class TransferStatus {

    public final static int STATUS_ID_PENDING = 1;
    public final static int STATUS_ID_APPROVED = 2;
    public final static int STATUS_ID_REJECTED = 3;
    private int transferStatusId;
    private String transferStatusDesc;

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public String getTransferStatusDesc() {
        return transferStatusDesc;
    }

    public void setTransferStatusDesc(String transferStatusDesc) {
        this.transferStatusDesc = transferStatusDesc;
    }
}













