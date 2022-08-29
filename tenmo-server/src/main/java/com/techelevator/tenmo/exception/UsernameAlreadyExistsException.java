package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.BAD_REQUEST, reason = "User Already Exists")
public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String errorMessage){
        super("This user name is already in use. Please choose another.");
    }
}