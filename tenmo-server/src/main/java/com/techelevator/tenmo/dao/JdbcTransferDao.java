package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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
    public Transfer findByTransferId(int id) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_to, account_from, amount " +
                "FROM transfer " +
                "WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        return rowSet.next() ? mapRowToTransfer(rowSet) : null;
    }

    @Override
    public void create(Transfer transfer) {

        String sql = "INSERT into transfer (account_from, transfer_type_id, transfer_status_id, account_to, amount) VALUES(?,?,?,?,?) RETURNING transfer_id;";
        transfer.setTransferID(jdbcTemplate.queryForObject(sql, Integer.class, transfer.getFromAccountID(),
                transfer.getTransferTypeID(), transfer.getTransferStatusID(), transfer.getToAccountID(),
                transfer.getTransferAmt()));
    }

    public List<Transfer> getTransferLog(int accountID) { //not sure how to implement this yet, but needed for cases 5 & 6
        List<Transfer> transferLog = new ArrayList<>();


        String sql = "select transfer_id, transfer_type_id, transfer_status_id, account_to, account_from, amount "+
                "from transfer as t "+
//                "left join on account as a"+
//                "on t.account_from = a.account_id"+
                "where t.account_to = ? or t.account_from= ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountID, accountID);

        while(results.next()){
            Transfer transfer = mapRowToTransfer(results);
            transferLog.add(transfer);
        }
        return transferLog;
    }

//        int accountId = getAccountByUserID(userID);
//        List<Transfer> transfers = new ArrayList<>();
//        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers WHERE account_from = ? OR account_to = ? AND transfer_status_id <> 1";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
//
//        while(results.next()) {
//            int transferId = results.getInt("transfer_id");
//            int transferType = results.getInt("transfer_type_id");
//            int transferStatusId = results.getInt("transfer_status_id");
//            int accountFrom = results.getInt("account_from");
//            int accountTo = results.getInt("account_to");
//
//            String otherName = "";
//            String sendOrReceive = "Send";
//            if(accountId == accountFrom){
//                otherName = getUserNameByAccountID(accountTo);
//
//            } else {
//                otherName = getUserNameByAccountID(accountFrom);
//                sendOrReceive = "Recieve";
//            }
//
//            BigDecimal amount = results.getBigDecimal("amount");
//
//            Transfer newTransfer = new Transfer(accountFrom, transferType, transferStatusId, accountTo, transferId, amount);
//            transfers.add(newTransfer);
//
//        }
//
//        return transfers;
//    }


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
    public void update(Transfer transfer) {
        String sql = "UPDATE transfer " +
                "SET transfer_type_id = ?, transfer_status_id = ?, account_from = ?, account_to = ?, amount = ? " +
                "WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transfer.getTransferTypeID(), transfer.getTransferStatusID(),
                transfer.getFromAccountID(),  transfer.getToAccountID(),  transfer.getTransferAmt(),
                transfer.getTransferID());
    }

    @Override
    public Transfer getTransferFromTransferId(int transferID) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_to, account_from, amount " +
                "FROM transfer " +
                "WHERE transfer_id = ?;";
        Transfer transfer = null;
        transfer= jdbcTemplate.queryForObject(sql,Transfer.class,transferID);
        return transfer;
    }

    @Override
    public String getUserNameByAccountID(int accountID) {
        String sql = "select username from tenmo_user JOIN account ON tenmo_user.user_id = account.user_id where account_id = ?;";
        String username = "";
        username = jdbcTemplate.queryForObject(sql, String.class, accountID);
        return username;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferID(rs.getInt("transfer_id"));
        transfer.setTransferTypeID(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusID(rs.getInt("transfer_status_id"));
        transfer.setFromAccountID(rs.getInt("account_from"));
        transfer.setToAccountID(rs.getInt("account_to"));
        transfer.setTransferAmt(rs.getBigDecimal("amount"));
        return transfer;
    }

    @Override
    public TransferStatus getTransferStatus(String status) {
        String sql = "select transfer_status_id, transfer_status_desc from transfer_status where transfer_status_desc = ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, status);
        TransferStatus transferStatus = null;
        if (rs.next()) {
            transferStatus = new TransferStatus();
            transferStatus.setTransferStatusId(rs.getInt("transfer_status_id"));
            transferStatus.setTransferStatusDesc(rs.getString("transfer_status_desc"));
        }
        return transferStatus;
    }

    @Override
    public TransferType getTransferType(String type) {
        String sql = "select transfer_type_id, transfer_type_desc from transfer_type where transfer_type_desc = ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, type);
        TransferType transferType = null;
        if (rs.next()) {
            transferType = new TransferType();
            transferType.setTransferTypeId(rs.getInt("transfer_type_id"));
            transferType.setTransferTypeDescription(rs.getString("transfer_type_desc"));
        }
        return transferType;
    }

    @Override
    public Transfer[]getUnresolvedTransfersViaUserId(int userID){
        Transfer[] unresolvedList = new Transfer[0];
List<Transfer> returnList = new ArrayList<>();
        String sql = "t.transfer_id, t.transfer_type_id, t.transfer_status_id, t.account_from, t.account_to, t.amount " +
                "from transfer as t" +
                "right join tenmo_user as tu " +
                "on tu.account_id = t.account_to " +
                "where tu.user_id = ?;";

                SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userID);
        while(results.next()){
           Transfer transfer = mapRowToTransfer(results);
            returnList.add(transfer);
        }
        for(int i =0; i < returnList.size(); i++){
            unresolvedList[i]=returnList.get(i);
        }
        return unresolvedList;
    }
}