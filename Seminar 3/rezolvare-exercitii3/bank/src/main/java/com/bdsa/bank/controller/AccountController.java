package com.bdsa.bank.controller;

import com.bdsa.bank.dto.TransferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bdsa.bank.dto.AccountRequest;
import com.bdsa.bank.dto.AccountResponse;
import com.bdsa.bank.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // GET /api/accounts
    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    // GET /api/accounts/1
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    // GET /api/accounts/iban/RO49AAAA...
    @GetMapping("/iban/{iban}")
    public ResponseEntity<AccountResponse> findByIban(@PathVariable String iban) {
        return ResponseEntity.ok(accountService.findByIban(iban));
    }

    // GET /api/accounts?status=ACTIVE
    @GetMapping(params = "status")
    public ResponseEntity<List<AccountResponse>> findByStatus(@RequestParam String status) {
        return ResponseEntity.ok(accountService.findByStatus(status));
    }

    // POST /api/accounts
    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody AccountRequest request) {
        AccountResponse created = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/accounts/1
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> update(@PathVariable Long id,
                                                  @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.update(id, request));
    }

    // PATCH /api/accounts/1/block
    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> block(@PathVariable Long id) {
        accountService.block(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/accounts/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<AccountResponse>> findTopByBalance(@RequestParam int limit){
        return ResponseEntity.ok(accountService.findTopByBalance(limit));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable Long id){
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable Long id){
        accountService.unblock(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest transferRequest){
        accountService.transfer(transferRequest);
        return ResponseEntity.ok().build();
    }
}

