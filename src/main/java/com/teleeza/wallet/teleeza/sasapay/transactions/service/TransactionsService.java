package com.teleeza.wallet.teleeza.sasapay.transactions.service;

import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import com.teleeza.wallet.teleeza.sasapay.transactions.repository.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionsService {
    @Autowired
    private TransactionsRepository transactionsRepository;

    @CacheEvict(value = "recent_transactions", allEntries = true)
    public List<Transactions> recentTransactions(String beneficiarryAccountNumber) {
        return transactionsRepository.getAllTransactionsLimitBy5(beneficiarryAccountNumber);
    }

    public Long sumOfTodaysIncome(String accountNumber){
        return transactionsRepository.sumOfTodaysIncome(accountNumber);
    }

    public Long sumOfTodaysExpense(String accountNumber){
        return transactionsRepository.sumOfTodaysExpenses(accountNumber);
    }
}
