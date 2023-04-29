package com.techelevator.dao;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {

    private JdbcAccountDao sut;
    private static final Account ACCOUNT_1 = new Account(1001, new BigDecimal("1000.00"), true);
    private static final Account ACCOUNT_2 = new Account(1002, new BigDecimal("1000.00"), true);
    private static final Account ACCOUNT_3 = new Account(1002, new BigDecimal("500.00"), false);

    @Before
    public void setup() {
        sut = new JdbcAccountDao(dataSource);
    }

    @Test
    public void getAccounts_returns_all_accounts_of_user_id() {
        List<Account> accounts = sut.getAccounts(1002);
        Assert.assertEquals(2, accounts.size());

        assertAccountsMatch(ACCOUNT_2, accounts.get(0));
        assertAccountsMatch(ACCOUNT_3, accounts.get(1));

        List<Account> accounts2 = sut.getAccounts(1001);
        Assert.assertEquals(1, accounts2.size());

        assertAccountsMatch(ACCOUNT_1, accounts2.get(0));
    }

    @Test
    public void getAccount_returns_account() {
        Account account = sut.getAccount(1001, 2001);
        assertAccountsMatch(ACCOUNT_1, account);

        Account account2 = sut.getAccount(1002, 2002);
        assertAccountsMatch(ACCOUNT_2, account2);

        Account account3 = sut.getAccount(1002, 2003);
        assertAccountsMatch(ACCOUNT_3, account3);
    }

    @Test
    public void createAccount_returns_account_with_id_and_expected_values() {
        Account testAccount = new Account(1001, new BigDecimal("1500.00"), false);
        Account createdAccount = sut.createAccount(testAccount);

        int newUserId = createdAccount.getUserId();
        int newAccId = createdAccount.getAccountId();
        Assert.assertTrue(newAccId >= 2001);
        Assert.assertTrue(newUserId >= 1001);

        Account retrievedAccount = sut.getAccount(newUserId, newAccId);
        assertAccountsMatch(createdAccount, retrievedAccount);
    }

    @Test
    public void getPrimaryAccountBalanceByUsername_returns_balance_and_expected_balance() {
        BigDecimal actualBalance = sut.getPrimaryAccountBalanceByUsername("bob");
        BigDecimal actualBalance2 = sut.getPrimaryAccountBalanceByUsername("user");
        BigDecimal actualBalance3 = sut.getPrimaryAccountBalanceByUsername("joe");

        Assert.assertEquals(new BigDecimal("1000.00"), actualBalance);
        Assert.assertEquals(new BigDecimal("1000.00"), actualBalance2);
        Assert.assertEquals(new BigDecimal("2000.00"), actualBalance3);
    }

    private void assertAccountsMatch(Account expected, Account actual) {
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getBalance(), actual.getBalance());
        Assert.assertEquals(expected.isPrimaryAccount(), actual.isPrimaryAccount());
    }
}