package com.bdsa.bank.dto;

import com.bdsa.bank.entity.Transaction;

import java.time.LocalDate;

public class TransactionResponse {
    private Long id;
    private Long accountId;
    private String type;
    private Double amount;
    private String description;
    private LocalDate transactionDate;

    public TransactionResponse() {}

    public TransactionResponse(com.bdsa.bank.entity.Transaction t) {
        this.id = t.getId();
        this.accountId = t.getAccount().getId();
        this.type = t.getType();
        this.amount = t.getAmount();
        this.description = t.getDescription();
        this.transactionDate = t.getTransactionDate();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
}