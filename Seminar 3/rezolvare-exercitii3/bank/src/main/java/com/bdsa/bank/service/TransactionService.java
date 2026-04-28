package com.bdsa.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bdsa.bank.dto.TransactionRequest;
import com.bdsa.bank.dto.TransactionResponse;
import com.bdsa.bank.entity.Account;
import com.bdsa.bank.entity.Transaction;
import com.bdsa.bank.exception.InsufficientFundsException;
import com.bdsa.bank.exception.ResourceNotFoundException;
import com.bdsa.bank.repository.AccountRepository;
import com.bdsa.bank.repository.TransactionRepository;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findByAccountId(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Contul cu ID " + accountId + " nu exista.");
        }
        return transactionRepository.findByAccountIdOrderedByDate(accountId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    public TransactionResponse create(TransactionRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contul cu ID " + request.getAccountId() + " nu exista."));

        if ("DEBIT".equals(request.getType()) && account.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException(
                    "Sold insuficient. Sold curent: " + account.getBalance() +
                            ", suma ceruta: " + request.getAmount()
            );
        }

        // Actualizeaza soldul
        if ("CREDIT".equals(request.getType())) {
            account.setBalance(account.getBalance() + request.getAmount());
        } else {
            account.setBalance(account.getBalance() - request.getAmount());
        }
        accountRepository.save(account);

        // Salveaza tranzactia
        Transaction transaction = new Transaction(
                account,
                request.getType(),
                request.getAmount(),
                request.getDescription()
        );
        Transaction saved = transactionRepository.save(transaction);
        return new TransactionResponse(saved);
    }

    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tranzactia cu ID " + id + " nu exista."));
        transactionRepository.delete(transaction);
    }

    public Map<String, Double> getSummary(Long accountId){
        if(!accountRepository.existsById(accountId)){
            throw new ResourceNotFoundException("Contul cu ID " + accountId + " nu exista.");
        }

        return Map.of(
                "totalDebit", transactionRepository.sumAmountByAccountAndType(accountId, "DEBIT"),
                "totalCredit", transactionRepository.sumAmountByAccountAndType(accountId, "CREDIT")
        );
    }
}