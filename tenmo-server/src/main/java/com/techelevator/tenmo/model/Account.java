package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    private BigDecimal balance;
}
