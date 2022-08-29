package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.InsufficientFundsException;
import com.techelevator.tenmo.exception.SelfPaymentException;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

// Controller to hold endpoints for http requests; all will need authentication
@RestController
@RequestMapping(path="/tenmo")
@PreAuthorize("isAuthenticated()")
public class TEnmoController {

    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    TEnmoController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "getbalance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principle) throws AccountNotFoundException {
        Account account = accountDao.findByUsername(principle.getName());
        if (account == null) {
            throw new AccountNotFoundException("Account for user \"" + principle.getName() + "\" was not found.");
        }
        return account.getBalance();
    }

    @RequestMapping(path = "getusers", method = RequestMethod.GET)
    public List<PublicUserInfoDTO> getUsers() throws AccountNotFoundException {
        return userDao.findAllPublic();
    }

    @RequestMapping(path = "account/user/{id}", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable int id) throws AccountNotFoundException {
        Account account = accountDao.findByUserId(id);
        if (account == null) {
            throw new AccountNotFoundException("Account for user with id=" + id + " was not found.");
        }
        return account;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer) {
        transferDao.create(transfer);
        if (transfer.getTransferTypeID() == TransferType.TYPE_ID_SEND
                && transfer.getTransferStatusID() == TransferStatus.STATUS_ID_APPROVED) {
            try {
                accountDao.transferMoney(transfer.getFromAccountID(), transfer.getToAccountID(), transfer.getTransferAmt());
            } catch (InsufficientFundsException | SelfPaymentException e) {
                transfer.setTransferStatusID(TransferStatus.STATUS_ID_REJECTED);
                try {
                    transferDao.update(transfer);
                }
                catch (Exception e2) {
                    throw new RuntimeException(e2);
                }
                throw new RuntimeException(e);
            }
        }
        return transfer;
    }

    @RequestMapping(path = "transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int id) {
        return transferDao.findByTransferId(id);
    }

    @RequestMapping(path = "transfer/{id}", method = RequestMethod.PUT)
    public Transfer updateTransfer(@Valid @RequestBody Transfer updatedTransfer, @PathVariable int id) {
        Transfer existingTransfer = transferDao.findByTransferId(updatedTransfer.getTransferID());
        if (existingTransfer.getTransferStatusID() != TransferStatus.STATUS_ID_APPROVED
            && updatedTransfer.getTransferStatusID() == TransferStatus.STATUS_ID_APPROVED) {
            try {
                accountDao.transferMoney(updatedTransfer.getFromAccountID(), updatedTransfer.getToAccountID(), updatedTransfer.getTransferAmt());
                transferDao.update(updatedTransfer);
            } catch (InsufficientFundsException | SelfPaymentException e) {
                updatedTransfer.setTransferStatusID(TransferStatus.STATUS_ID_REJECTED);
                try {
                    transferDao.update(updatedTransfer);
                }
                catch (Exception e2) {
                    throw new RuntimeException(e2);
                }
                throw new RuntimeException(e);
            }
        }
        return updatedTransfer;
    }

    @RequestMapping(path="gettransfers", method = RequestMethod.GET)
    public List<Transfer> getTransferLog(Principal principal) throws AccountNotFoundException {
        Account account = accountDao.findByUsername(principal.getName());
        return transferDao.getTransferLog(account.getAccountId());
    }

    @RequestMapping(path="gettransferstatusid/{status}", method = RequestMethod.GET)
    TransferStatus getTransferStatusId(@PathVariable String status) {
        return transferDao.getTransferStatus(status);
    }

    @RequestMapping(path="gettransfertypeid/{type}", method = RequestMethod.GET)
    TransferType getTransferTypeId(@PathVariable String type) {
        return transferDao.getTransferType(type);
    }

    @RequestMapping(path="userbyaccountid/{accountID}", method = RequestMethod.GET)
    User findUsernameByAccountID(@PathVariable int accountID) {
        return userDao.findUsernameByAccountID(accountID);
    }
    @RequestMapping(path="user/{id}", method = RequestMethod.GET)
    User getUserViaUserId(@PathVariable int id) {
        return userDao.getUserViaUserId(id);
    }
}