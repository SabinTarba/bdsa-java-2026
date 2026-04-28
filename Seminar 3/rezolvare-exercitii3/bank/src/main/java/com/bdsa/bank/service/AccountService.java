package com.bdsa.bank.service;

import com.bdsa.bank.dto.TransferRequest;
import com.bdsa.bank.entity.Transaction;
import com.bdsa.bank.exception.InvalidAccountStatusException;
import com.bdsa.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bdsa.bank.dto.AccountRequest;
import com.bdsa.bank.dto.AccountResponse;
import com.bdsa.bank.entity.Account;
import com.bdsa.bank.exception.InsufficientFundsException;
import com.bdsa.bank.exception.ResourceNotFoundException;
import com.bdsa.bank.repository.AccountRepository;

import java.util.List;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(AccountResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + id + " nu exista."));
        return new AccountResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse findByIban(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu IBAN " + iban + " nu exista."));
        return new AccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findByStatus(String status) {
        return accountRepository.findByStatus(status)
                .stream()
                .map(AccountResponse::new)
                .toList();
    }

    public AccountResponse create(AccountRequest request) {
        Account account = new Account(
                request.getIban(),
                request.getOwnerName(),
                request.getBalance() != null ? request.getBalance() : 0.0,
                request.getCurrency() != null ? request.getCurrency() : "RON"
        );
        Account saved = accountRepository.save(account);
        return new AccountResponse(saved);
    }

    public AccountResponse update(Long id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + id + " nu exista."));

        if (request.getOwnerName() != null) account.setOwnerName(request.getOwnerName());
        if (request.getCurrency() != null) account.setCurrency(request.getCurrency());

        Account saved = accountRepository.save(account);
        return new AccountResponse(saved);
    }

    public void block(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + id + " nu exista."));
        account.setStatus("BLOCKED");
        accountRepository.save(account);
    }

    public void delete(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + id + " nu exista."));

        if (account.getBalance() > 0) {
            throw new InsufficientFundsException(
                    "Contul nu poate fi sters. Soldul trebuie sa fie 0. Sold curent: " + account.getBalance()
            );
        }

        accountRepository.delete(account);
    }

    public List<AccountResponse> findTopByBalance(int limit){
        return accountRepository.findTopByBalance(limit).stream().map(AccountResponse::new).toList();
    }

    public Double getBalance(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + accountId + " nu exista."));

        return account.getBalance();
    }

    public void unblock(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + accountId + " nu exista."));

        if(!account.getStatus().equals("BLOCKED")){
            throw new InvalidAccountStatusException("Statusul contului nu este BLOCKED");
        }

        account.setStatus("ACTIVE");

        accountRepository.save(account);
    }

    @Transactional
    public void transfer(TransferRequest transferRequest){
        Long fromAccountId = transferRequest.getFromAccountId();
        Long toAccountId = transferRequest.getToAccountId();

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + fromAccountId + " (FROM) nu exista."));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Contul cu ID " + toAccountId + " (TO) nu exista."));

        Double amount = transferRequest.getAmount();

        if(fromAccount.getBalance() < amount){
            throw new InsufficientFundsException("Fonduri insuficiente pentru transfer");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transactionRepository.save(new Transaction(fromAccount, "DEBIT", amount, transferRequest.getDescription()));
        transactionRepository.save(new Transaction(toAccount, "CREDIT", amount, transferRequest.getDescription()));
    }
}