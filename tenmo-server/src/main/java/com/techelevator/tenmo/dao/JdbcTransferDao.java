package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    //actual methods called for each database interaction in the "transfer" table
    private JdbcTemplate jdbcTemplate;
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void create(Transfer transfer) {

        String sql = "INSERT into transfer (account_from, transfer_type_id, transfer_status_id, account_to, amount) VALUES(?,?,?,?,?) RETURNING transfer_id;";
        transfer.setTransferID(jdbcTemplate.queryForObject(sql, Integer.class, transfer.getFromAccountID(),
                transfer.getTransferTypeID(), transfer.getTransferStatusID(), transfer.getToAccountID(),
                transfer.getTransferAmt()));
    }

    @Override
    public List<Transfer> getTransferLog(int userID) { //not sure how to implement this yet, but needed for cases 5 & 6
        return null;
    }

    @Override
    public int getAccountByUserID(int userID) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        int accountId = 0;
        accountId = jdbcTemplate.queryForObject(sql, int.class, userID);
        return accountId;
    }

    @Override
    public int getAccountByUserName(String username) {
        String sql = "SELECT account_id FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE username = ?;";
        int accountId = 0;
        accountId = jdbcTemplate.queryForObject(sql, int.class, username);
        return accountId;
    }

    @Override
    public String getUserNameByAccountID(int accountID) {
        String sql = "select username from tenmo_user JOIN account ON tenmo_user.user_id = account.user_id where account_id = ?;";
        String username = "";
        username = jdbcTemplate.queryForObject(sql, String.class, username);
        return username;
    }

}
