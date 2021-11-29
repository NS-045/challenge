package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferDto;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InvalidValueException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;

import com.db.awmd.challenge.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Mock
  NotificationService notificationService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }

  @Test
  public void testTransact_transferAmountisZero() throws Exception {
    Account account1 = new Account("Id1", BigDecimal.valueOf(10));
    Account account2 = new Account("Id2", BigDecimal.valueOf(20));
    this.accountsService.createAccount(account1);
    this.accountsService.createAccount(account2);
    TransferDto transferDto = new TransferDto("Id1", "Id2", BigDecimal.ZERO);
    try {
      this.accountsService.transact(transferDto);
    } catch (InvalidValueException exp) {
      assertThat(exp.getMessage()).isEqualTo("TransferAmout should be greater than 0");
    }
  }

  @Test
  public void testTransact_transferAmountisnegative() throws Exception {
    Account account1 = new Account("Id3", BigDecimal.valueOf(10));
    Account account2 = new Account("Id4", BigDecimal.valueOf(20));
    this.accountsService.createAccount(account1);
    this.accountsService.createAccount(account2);
    TransferDto transferDto = new TransferDto("Id3", "Id4", BigDecimal.valueOf(-10));
    try {
      this.accountsService.transact(transferDto);
    } catch (InvalidValueException exp) {
      assertThat(exp.getMessage()).isEqualTo("TransferAmout should be greater than 0");
    }
  }

  @Test
  public void testTransact_transferAmountisLessThanAccountBalance() throws Exception {
    Account account1 = new Account("Id5", BigDecimal.valueOf(10));
    Account account2 = new Account("Id6", BigDecimal.valueOf(20));
    this.accountsService.createAccount(account1);
    this.accountsService.createAccount(account2);
    TransferDto transferDto = new TransferDto("Id5", "Id6", BigDecimal.valueOf(20));
    try {
      this.accountsService.transact(transferDto);
    } catch (InvalidValueException exp) {
      assertThat(exp.getMessage()).isEqualTo("Innsufficient balance to make the transfer");
    }
  }

  @Test
  public void testTransact_transferAmountisValid() throws Exception {
    Account account1 = new Account("Id7", BigDecimal.valueOf(10));
    Account account2 = new Account("Id8", BigDecimal.valueOf(20));
    this.accountsService.createAccount(account1);
    this.accountsService.createAccount(account2);
    TransferDto transferDto = new TransferDto("Id7", "Id8", BigDecimal.valueOf(5));
    this.accountsService.transact(transferDto);

    assertThat(this.accountsService.getAccount("Id7").getBalance()).isEqualTo(BigDecimal.valueOf(5));
    assertThat(this.accountsService.getAccount("Id8").getBalance()).isEqualTo(BigDecimal.valueOf(25));
  }

}
