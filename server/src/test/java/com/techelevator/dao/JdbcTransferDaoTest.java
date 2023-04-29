package com.techelevator.dao;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransferDaoTest extends BaseDaoTests {

    private JdbcTransferDao sut;
    private JdbcUserDao userDao;
    private static final Transfer TRANSFER_1 = new Transfer(2001, 2002, new BigDecimal("200.00"), false);
    private static final Transfer TRANSFER_2 = new Transfer(2002, 2001, new BigDecimal("400.00"), true);
    private static final Transfer TRANSFER_3 = new Transfer(2004, 2002, new BigDecimal("500.00"), false);
    private static final Transfer TRANSFER_4 = new Transfer(2001, 2004, new BigDecimal("100.00"), true);

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        userDao = new JdbcUserDao(jdbcTemplate, new JdbcAccountDao(dataSource));
        sut = new JdbcTransferDao(dataSource, userDao);
    }

    @Test
    public void sendTransfer() {
        Transfer createdTransfer = sut.sendTransfer(TRANSFER_1);
        int transferId = createdTransfer.getTransferId();
        Transfer retrievedTransfer = sut.getTransferById(transferId);

        Assert.assertTrue(transferId >= 3001);
        assertTransfersMatch(createdTransfer, retrievedTransfer);
    }

    @Test
    public void getTransfersByUser_return_all_users_transfers() {
        List<Transfer> transfers = sut.getTransfersByUser(1001);
        Assert.assertEquals(3, transfers.size());

        assertTransfersMatch(TRANSFER_1, transfers.get(0));
        assertTransfersMatch(TRANSFER_2, transfers.get(1));
        assertTransfersMatch(TRANSFER_4, transfers.get(2));
    }

    @Test
    public void getTransferById_returns_specfic_transfer() {
        Transfer transfer = sut.getTransferById(3004);
        Transfer transfer2 = sut.getTransferById(3002);
        Transfer transfer3 = sut.getTransferById(3003);

        assertTransfersMatch(TRANSFER_4, transfer);
        assertTransfersMatch(TRANSFER_2, transfer2);
        assertTransfersMatch(TRANSFER_3, transfer3);
    }

    @Test
    public void getPendingTransfersByAccount() {
        List<Transfer> transfers = sut.getPendingTransfersByAccount(1001);
        Assert.assertEquals(2, transfers.size());

        assertTransfersMatch(TRANSFER_2, transfers.get(0));
        assertTransfersMatch(TRANSFER_4, transfers.get(1));
    }

    @Test
    public void createTransfer_returns_id_and_expected_values() {
        BigDecimal amount = new BigDecimal("100.50");
        Transfer testTransfer = new Transfer();
        testTransfer.setAccountIdReceiving(2002);
        testTransfer.setAccountIdSending(2004);
        testTransfer.setAmount(amount);
        testTransfer.setPending(true);

        Transfer createdTransfer = sut.createTransfer(testTransfer);
        int newId = createdTransfer.getTransferId();
        Assert.assertTrue(newId > 3000);

        Transfer retrievedTransfer = sut.getTransferById(newId);
        Assert.assertEquals(createdTransfer.getAccountIdSending(), retrievedTransfer.getAccountIdSending());
        Assert.assertEquals(createdTransfer.getAccountIdReceiving(), retrievedTransfer.getAccountIdReceiving());
        Assert.assertEquals(createdTransfer.getAmount(), retrievedTransfer.getAmount());
        Assert.assertEquals(createdTransfer.isPending(), retrievedTransfer.isPending());
    }

    @Test
    public void approveTransfer_updates_balances() {
        BigDecimal amount = new BigDecimal("100.50");
        Transfer testTransfer = new Transfer();
        testTransfer.setAccountIdReceiving(2002);
        testTransfer.setAccountIdSending(2004);
        testTransfer.setAmount(amount);
        testTransfer.setPending(true);

        AccountDao accountDao = new JdbcAccountDao(dataSource);
        BigDecimal userBalanceBefore = accountDao.getPrimaryAccountBalanceByUsername("user");
        BigDecimal joeBalanceBefore = accountDao.getPrimaryAccountBalanceByUsername("joe");

        sut.approveTransferRequest(testTransfer);
        BigDecimal userBalanceAfter = accountDao.getPrimaryAccountBalanceByUsername("user");
        BigDecimal joeBalanceAfter = accountDao.getPrimaryAccountBalanceByUsername("joe");

        Assert.assertEquals(joeBalanceAfter, joeBalanceBefore.subtract(testTransfer.getAmount()));
        Assert.assertEquals(userBalanceAfter, userBalanceBefore.add(testTransfer.getAmount()));
    }

    @Test
    public void rejected_transfer_cannot_be_retrieved() {
        sut.rejectTransferRequest(3002);
        Transfer retrievedTransfer = sut.getTransferById(3002);
        Assert.assertNull(retrievedTransfer);
    }

    @Test
    public void mapTransferToTransferDTO_returns_expected_values() {
        BigDecimal amount = new BigDecimal("100.50");
        Transfer testTransfer = new Transfer();
        testTransfer.setTransferId(3005);
        testTransfer.setAccountIdReceiving(2002);
        testTransfer.setAccountIdSending(2004);
        testTransfer.setAmount(amount);
        testTransfer.setPending(false);

        TransferDTO createdDTO = new TransferDTO("joe", "user", amount, "Approved", 3005);
        TransferDTO mappedDTO = sut.mapTransferToTransferDTO(testTransfer);
        Assert.assertEquals(createdDTO.getUserSending(), mappedDTO.getUserSending());
        Assert.assertEquals(createdDTO.getUserReceiving(), mappedDTO.getUserReceiving());
        Assert.assertEquals(createdDTO.getAmount(), mappedDTO.getAmount());
        Assert.assertEquals(createdDTO.getTransferId(), mappedDTO.getTransferId());
        Assert.assertEquals(createdDTO.getStatus(), mappedDTO.getStatus());
    }

    @Test
    public void mapTransferDTOToTransfer_has_expected_values() {
        BigDecimal amount = new BigDecimal("200");
        TransferDTO testDTO = new TransferDTO("bob", "joe", amount);

        Transfer createdTransfer = new Transfer();
        createdTransfer.setAccountIdSending(2001);
        createdTransfer.setAccountIdReceiving(2004);
        createdTransfer.setAmount(amount);
        createdTransfer.setPending(true);

        Transfer mappedTransfer = sut.mapTransferDTOToTransfer(testDTO);
        Assert.assertEquals(createdTransfer.getAccountIdSending(), mappedTransfer.getAccountIdSending());
        Assert.assertEquals(createdTransfer.getAccountIdReceiving(), mappedTransfer.getAccountIdReceiving());
        Assert.assertEquals(createdTransfer.getAmount(), mappedTransfer.getAmount());
        Assert.assertEquals(createdTransfer.isPending(), mappedTransfer.isPending());
    }

    @Test
    public void testMapTransferDTOToTransfer_has_expected_values_with_id_param() {
        BigDecimal amount = new BigDecimal("0.50");
        TransferDTO testDTO = new TransferDTO("bob", "joe", amount);

        Transfer createdTransfer = new Transfer();
        createdTransfer.setTransferId(3007);
        createdTransfer.setAccountIdSending(2001);
        createdTransfer.setAccountIdReceiving(2004);
        createdTransfer.setAmount(amount);
        createdTransfer.setPending(false);

        Transfer mappedTransfer = sut.mapTransferDTOToTransfer(testDTO, 3007);
        Assert.assertEquals(createdTransfer.getTransferId(), mappedTransfer.getTransferId());
        Assert.assertEquals(createdTransfer.getAccountIdSending(), mappedTransfer.getAccountIdSending());
        Assert.assertEquals(createdTransfer.getAccountIdReceiving(), mappedTransfer.getAccountIdReceiving());
        Assert.assertEquals(createdTransfer.getAmount(), mappedTransfer.getAmount());
        Assert.assertEquals(createdTransfer.isPending(), mappedTransfer.isPending());
    }

    @Test
    public void getPrimaryAccountIDFromUsername_retrieves_correct_id() {
        int createdId = 2001;
        int retrievedId = sut.getPrimaryAccountIDFromUsername("bob");
        Assert.assertEquals(createdId, retrievedId);

        int createdId2 = 2004;
        int retrievedId2 = sut.getPrimaryAccountIDFromUsername("joe");
        Assert.assertEquals(createdId2, retrievedId2);
    }

    @Test
    public void updateStatus_updates_status_to_approved() {
        Transfer testTransfer = sut.getTransferById(3002);

        boolean updated = sut.updateStatus(testTransfer);
        Assert.assertTrue(updated);

        Transfer updatedTransfer = sut.getTransferById(testTransfer.getTransferId());
        boolean isPendingTest = updatedTransfer.isPending();
        Assert.assertFalse(isPendingTest);
    }

    public void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getAccountIdSending(), actual.getAccountIdSending());
        Assert.assertEquals(expected.getAccountIdReceiving(), actual.getAccountIdReceiving());
        Assert.assertEquals(expected.getAmount(), actual.getAmount());
        Assert.assertEquals(expected.isPending(), actual.isPending());
    }
}