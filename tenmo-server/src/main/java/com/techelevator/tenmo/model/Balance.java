package com.techelevator.tenmo.model;

import com.techelevator.tenmo.exception.InsufficientFundsException;

import java.math.BigDecimal;

public class Balance {
    private BigDecimal balance;

    public BigDecimal getBalance(){
        return balance;
    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }

    public void yeetFunds(BigDecimal ducets) throws InsufficientFundsException {
        BigDecimal remainder = new BigDecimal(String.valueOf(balance)).subtract(ducets);
        if (remainder.compareTo(BigDecimal.ZERO) >= 0) {
            this.balance = remainder;
        } else {
            throw new InsufficientFundsException();
        }
    }

    public void randyMoss(BigDecimal ducets) {
        this.balance = new BigDecimal(String.valueOf(balance)).add(ducets);
    }

}
