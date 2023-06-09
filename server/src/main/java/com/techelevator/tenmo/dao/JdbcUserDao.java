package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserInfoDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcUserDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
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

    public String findUsernameById(int id) {
        String sql = "SELECT username FROM tenmo_user WHERE user_id = ?;";

        String username = jdbcTemplate.queryForObject(sql, String.class, id);
        return username;
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
        BigDecimal balance = new BigDecimal("1000.00");
        Account account = new Account(newUserId, balance, true);
        accountDao.createAccount(account);

        return true;
    }

    public String getUsernameByAccountId(int id) {
        User user = null;
        String username = "";

        String sql = "SELECT user_id, username, password_hash FROM tenmo_user " +
                     "WHERE user_id IN (SELECT user_id FROM account WHERE account_id = ?)";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            user = mapRowToUser(results);
        }

        username = user.getUsername();
        return username;
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

    public UserInfoDTO mapUserToUserInfoDTO(User user){
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername());
        return userInfoDTO;
    }
}
