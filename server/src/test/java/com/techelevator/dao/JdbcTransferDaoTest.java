package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.*;

public class JdbcTransferDaoTest extends BaseDaoTests {

    private JdbcTransferDao sut;
    private JdbcUserDao userDao;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(dataSource, userDao);
    }

    @Test
    public void sendTransfer() {
    }

    @Test
    public void getTransfersByUser() {
    }

    @Test
    public void getTransferById() {
    }

    @Test
    public void getPendingTransfersByAccount() {
    }

    // Ava

    @Test
    public void createTransfer() {
    }

    @Test
    public void approveTransferRequest() {
    }

    @Test
    public void rejectTransferRequest() {
    }

    @Test
    public void mapTransferToTransferDTO() {
    }

    @Test
    public void mapTransferDTOToTransfer() {
    }

    @Test
    public void testMapTransferDTOToTransfer() {
    }

    @Test
    public void getPrimaryAccountIDFromUsername() {
    }

    @Test
    public void updateStatus() {
    }
}