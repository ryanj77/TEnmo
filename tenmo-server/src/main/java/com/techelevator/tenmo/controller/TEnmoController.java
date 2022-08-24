package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

// Controller to hold endpoints for http requests; all will need authentication
@RestController
@RequestMapping(path="/tenmo")
@PreAuthorize("isAuthenticated()")
public class TEnmoController {

    private AccountDao accountDao;

    TEnmoController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path="getbalance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principle) throws Exception {
        Account account = accountDao.findByUsername(principle.getName());
        return account.getBalance();
    }
}