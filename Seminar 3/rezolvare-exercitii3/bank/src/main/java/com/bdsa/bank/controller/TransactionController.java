package com.bdsa.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bdsa.bank.dto.TransactionRequest;
import com.bdsa.bank.dto.TransactionResponse;
import com.bdsa.bank.service.TransactionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // GET /api/transactions?accountId=1
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findByAccountId(@RequestParam Long accountId) {
        return ResponseEntity.ok(transactionService.findByAccountId(accountId));
    }

    // POST /api/transactions
    @PostMapping
    public ResponseEntity<TransactionResponse> create(@RequestBody TransactionRequest request) {
        TransactionResponse created = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE /api/transactions/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary(@RequestParam Long accountId) {
        return ResponseEntity.ok(transactionService.getSummary(accountId));
    }
}