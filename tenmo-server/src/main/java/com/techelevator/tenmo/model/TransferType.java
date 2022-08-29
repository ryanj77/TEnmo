package com.techelevator.tenmo.model;

public class TransferType {

    public final static int TYPE_ID_REQUEST = 1;
    public final static int TYPE_ID_SEND = 2;

    private int transferTypeId;
    private String transferTypeDescription;

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public String getTransferTypeDescription() {
        return transferTypeDescription;
    }

    public void setTransferTypeDescription(String transferTypeDescription) {
        this.transferTypeDescription = transferTypeDescription;
    }
}