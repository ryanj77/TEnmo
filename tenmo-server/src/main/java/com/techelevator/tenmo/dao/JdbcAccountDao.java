package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.InsufficientFundsException;
import com.techelevator.tenmo.exception.SelfPaymentException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// actual methods to be called for each database interaction with the "account" table
@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findByUsername(String username) {
        String sql = "SELECT account_id, account.user_id as user_id, balance " +
                "FROM account " +
                "JOIN tenmo_user on tenmo_user.user_id = account.user_id " +
                "WHERE tenmo_user.username=?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        return rowSet.next() ? mapRowToAccount(rowSet) : null;
    }

    @Override
    public Account findByAccountId(int id) {
        String sql = "SELECT account_id, user_id, balance " +
                "FROM account " +
                "WHERE account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        return rowSet.next() ? mapRowToAccount(rowSet) : null;
    }

    @Override
    public Account findByUserId(int id) {
        String sql = "SELECT account_id, user_id, balance " +
                "FROM account " +
                "WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        return rowSet.next() ? mapRowToAccount(rowSet) : null;
    }

    @Override
    public void transferMoney(int fromAccountId, int toAccountId, BigDecimal amount) throws InsufficientFundsException, SelfPaymentException {
        if (fromAccountId == toAccountId) {
            throw new SelfPaymentException();
        }
        Account fromAccount = findByAccountId(fromAccountId);
        Account toAccount = findByAccountId(toAccountId);
        if (amount.compareTo(fromAccount.getBalance()) > 0) {
            throw new InsufficientFundsException();
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        update(fromAccount);
        update(toAccount);
    }

    private void update(Account account) {
        String sql = "UPDATE account " +
                "SET account_id = ?, user_id = ?, balance = ? " +
                "WHERE account_id = ?";
        jdbcTemplate.update(sql, account.getAccountId(), account.getUserId(), account.getBalance(),
                account.getAccountId());
    }

    @Override
    public Account findUsernameByAccountID(int accountID){
        String sql = "select tu.username " +
                "from tenmo_user as tu "+
                "inner join account as a "+
                "on a.user_id = tu.user_id "+
                "where a.account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountID);
        return rowSet.next() ? mapRowToAccount(rowSet) : null;
    }


    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}