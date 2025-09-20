package com.teleeza.wallet.teleeza.sasapay.transactions.repository;

import com.teleeza.wallet.teleeza.sasapay.transactions.entity.TransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllTransactionsRepository extends JpaRepository<TransactionsEntity,Long> {
    TransactionsEntity findByMerchantRequestId(String merchantRequestId);

    TransactionsEntity findByBeneficiaryAccNumber(String beneficiarryAccNumber);

    @Query( value = "select * from all_transactions a where beneficiary_acc_number=?1 and result_code='0' and is_transaction_type = true order by id desc",nativeQuery = true)
    List<TransactionsEntity> getAllIncomeTransactions(String beneficiarryAccNumber);

    @Query( value = "select * from all_transactions a where beneficiary_acc_number=?1 and result_code='0'and is_transaction_type = false order by id desc",nativeQuery = true)
    List<TransactionsEntity> getAllExpenseTransactions(String beneficiaryAccNumber);

    @Query( value = "select * from all_transactions where beneficiary_acc_number=?1 and result_code='0' order by id desc limit  5;",nativeQuery = true)
//    @Query( value = "select * from all_transactions where beneficiary_acc_number=?1 and result_code='0'order by id desc limit  5;",nativeQuery = true)
    List<TransactionsEntity> getAllTransactionsLimitBy5(String beneficciaryAccNumber);
}
