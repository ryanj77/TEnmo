package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.BAD_REQUEST, reason = "From and to accounts cannot be the same.")

public class SelfPaymentException extends Exception{
    public SelfPaymentException(){
        super("From and to accounts cannot be the same.");
    }
}