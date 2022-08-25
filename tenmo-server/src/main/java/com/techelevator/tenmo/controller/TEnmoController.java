package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.PublicUserInfoDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    TEnmoController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path="getbalance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principle) throws AccountNotFoundException {
        Account account = accountDao.findByUsername(principle.getName());
        return account.getBalance();
    }

    @RequestMapping(path="getusers", method = RequestMethod.GET)
    public List<PublicUserInfoDTO> getUsers() throws AccountNotFoundException {
        return userDao.findAllPublic();
    }
}