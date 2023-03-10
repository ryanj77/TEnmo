package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.PublicUserInfoDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {
//actual methods called for each database interaction with the "tenmo_user" table


    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public List<PublicUserInfoDTO> findAllPublic() {
        List<PublicUserInfoDTO> userInfos = new ArrayList<>();
        String sql = "SELECT user_id, username FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            PublicUserInfoDTO userInfo = mapRowToPublicUserInfo(results);
            userInfos.add(userInfo);
        }
        return userInfos;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    @Override
    public User findUsernameByAccountID(int accountID){
        String sql = "select username, tu.user_id as user_id, password_hash " +
                "from tenmo_user as tu "+
                "inner join account as a "+
                "on a.user_id = tu.user_id "+
                "where a.account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountID);
        return rowSet.next() ? mapRowToUser(rowSet) : null;
    }

    @Override
    public User getUserViaUserId(int id) {
        String sql = "select  user_id, username, password_hash " +
                "from tenmo_user "+
                "where user_id= ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        return rowSet.next() ? mapRowToUser(rowSet) : null;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }

    private PublicUserInfoDTO mapRowToPublicUserInfo(SqlRowSet rs) {
        PublicUserInfoDTO userInfo = new PublicUserInfoDTO();
        userInfo.setId(rs.getInt("user_id"));
        userInfo.setUsername(rs.getString("username"));
        return userInfo;
    }
}