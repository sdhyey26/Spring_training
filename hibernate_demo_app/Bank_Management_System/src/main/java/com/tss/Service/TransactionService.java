package com.tss.Service;

import com.tss.Entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getByAccount(String accountNumber);
}


