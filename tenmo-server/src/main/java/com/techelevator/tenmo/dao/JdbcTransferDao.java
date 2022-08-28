package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public List<Transfer> getTransferLog(int userID) { //not sure how to implement this yet, but needed for cases 5 & 6
        int accountId = getAccountByUserID(userID);
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers WHERE account_from = ? OR account_to = ? AND transfer_status_id <> 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);

        while(results.next()) {


            int transferId = results.getInt("transfer_id");

            int transferType = results.getInt("transfer_type_id");
            int transferStatusId = results.getInt("transfer_status_id");


            int accountFrom = results.getInt("account_from");
            int accountTo = results.getInt("account_to");

            String otherName = "";
            String sendOrReceive = "Send";
            if(accountId == accountFrom){
                otherName = getUserNameByAccountID(accountTo);

            } else {
                otherName = getUserNameByAccountID(accountFrom);
                sendOrReceive = "Recieve";
            }

            BigDecimal amount = results.getBigDecimal("amount");

            Transfer newTransfer = new Transfer(accountFrom, transferType, transferStatusId, accountTo, transferId, amount);
            transfers.add(newTransfer);

        }

        return transfers;
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
