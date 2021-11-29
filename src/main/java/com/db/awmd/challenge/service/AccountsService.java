package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferDto;
import com.db.awmd.challenge.exception.InvalidValueException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  @Autowired
  private NotificationService notificationService;

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transact(TransferDto transferDto) {
    if(transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidValueException("TransferAmout should be greater than 0");
    }
    Account accountToTransferFrom = getAccount(transferDto.getAccountFromId());
    if(accountToTransferFrom.getBalance().compareTo(transferDto.getAmount()) == -1) {
      throw new InvalidValueException("Innsufficient balance to make the transfer");
    }
    Account accountToTransferTo = getAccount(transferDto.getAccountToId());

    //Update Account dto of both involved accounts
    accountToTransferFrom.setBalance(accountToTransferFrom.getBalance().subtract(transferDto.getAmount()));
    accountToTransferTo.setBalance(accountToTransferTo.getBalance().add(transferDto.getAmount()));

    //Update via repo call
    this.accountsRepository.updateAmount(accountToTransferFrom);
    this.accountsRepository.updateAmount(accountToTransferTo);

    //Notify
    notificationService.notifyAboutTransfer(accountToTransferFrom, transferDto.getAmount()+" Transferd to "+ transferDto.getAccountToId());
    notificationService.notifyAboutTransfer(accountToTransferFrom, transferDto.getAmount()+" Received from "+ transferDto.getAccountFromId());

  }
}
