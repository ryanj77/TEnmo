package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Transfer not located")

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(String errorMessage){
        super("Apologies. Transfer could not be found.");
    }
}
