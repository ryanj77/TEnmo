package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.UNAUTHORIZED, reason = "Approval denied. Only the user associated with the 'from account' is allowed to approve a pending request.")
public class UnauthorizedTransferApprovalException extends Exception {
    public UnauthorizedTransferApprovalException(){
        super("Approval denied. Only the user associated with the 'from account' is allowed to approve a pending request.");
    }
}