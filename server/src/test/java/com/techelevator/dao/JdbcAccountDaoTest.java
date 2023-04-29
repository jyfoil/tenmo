package com.techelevator.dao;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(dataSource);
    }

    @Test
    public void getAccounts() {
    }

    @Test
    public void getAccount() {
    }

    @Test
    public void createAccount() {
    }

    @Test
    public void getPrimaryAccountBalanceByUsername() {
    }
}