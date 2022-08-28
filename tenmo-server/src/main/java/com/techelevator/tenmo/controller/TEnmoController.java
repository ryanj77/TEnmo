package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.PublicUserInfoDTO;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public Account getAccountByUserId(@PathVariable long id) throws AccountNotFoundException {
        Account account = accountDao.findByUserId(id);
        if (account == null) {
            throw new AccountNotFoundException("Account for user with id=" + id + " was not found.");
        }
        return account;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public void createTransfer(@RequestBody Transfer transfer) {
        transferDao.create(transfer);

        if (transfer.getTransferStatusID() == 2) {
            // TODO Actually perform the transfer (update the account records) because the transfer status is already approved
            // Should check the from account balance here on the server too and, if insufficient, fail the transfer by changing the status to rejected
        }
    }
    @RequestMapping(path="gettransfers", method = RequestMethod.GET)
    public List<Transfer> getTransferLog(Principal principal) throws AccountNotFoundException {
        Account account = accountDao.findByUsername(principal.getName());
        return transferDao.getTransferLog(account.getAccountId());
    }

}