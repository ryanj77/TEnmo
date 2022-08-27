package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Funds unavailable")

public class InsufficientFundsException extends Exception{
    public InsufficientFundsException(){
        super("Your needs exceed your resources. Please adjust your request.");
    }
}
