package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

// actual methods to be called for each database interaction with the "account" table
@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findByUsername(String username) throws Exception {
        String sql = "SELECT account_id, account.user_id as user_id, balance " +
                "FROM account " +
                "JOIN tenmo_user on tenmo_user.user_id = account.user_id " +
                "WHERE tenmo_user.username=?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (!rowSet.next()) {
            throw new AccountNotFoundException(username);
        }
        return mapRowToAccount(rowSet);
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}