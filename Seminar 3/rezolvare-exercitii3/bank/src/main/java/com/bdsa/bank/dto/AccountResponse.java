package com.bdsa.bank.dto;

import com.bdsa.bank.entity.Account;

import java.time.LocalDate;

public class AccountResponse {
    private Long id;
    private String iban;
    private String ownerName;
    private Double balance;
    private String currency;
    private String status;
    private LocalDate createdAt;

    public AccountResponse() {}

    // Constructor de mapare din entity
    public AccountResponse(Account account) {
        this.id = account.getId();
        this.iban = account.getIban();
        this.ownerName = account.getOwnerName();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.status = account.getStatus();
        this.createdAt = account.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}