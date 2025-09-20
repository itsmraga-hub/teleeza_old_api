package com.teleeza.wallet.teleeza.sasapay.transactions.repository;

import com.teleeza.wallet.teleeza.sasapay.transactions.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Transactions findByMerchantRequestId(String merchantRequestId);

    @Query(value = "select * from validated_transactions a where beneficiary_acc_number= ?1 and transaction_amount is not null order by id desc limit  5;", nativeQuery = true)
    List<Transactions> getAllTransactionsLimitBy5(String accountNumber);

    @Query(value = "select * from validated_transactions a where beneficiary_acc_number=?1 and is_transaction_type = true and  transaction_amount is not null  order by id desc limit 100", nativeQuery = true)
    List<Transactions> getAllIncomeTransactions(String beneficiarryAccNumber);

    @Query(value = "select * from validated_transactions a where beneficiary_acc_number=?1 and is_transaction_type = false and  transaction_amount is not null order by id desc limit 100", nativeQuery = true)
    List<Transactions> getAllExpenseTransactions(String beneficiaryAccNumber);

    //    @Query("select sum(transaction_amount) from validated_transactions where DATE(created_at) = CURDATE() and is_transaction_type=true and sender_account_number='669994-213'")
    @Query(value = "SELECT sum(transaction_amount)  from validated_transactions where date (`created_at`)=current_date and is_transaction_type= true  and beneficiary_acc_number=?1 ", nativeQuery = true)
    Long sumOfTodaysIncome(String accNumber);

    @Query(value = "SELECT sum(transaction_amount)  from validated_transactions where date (`created_at`)=current_date and is_transaction_type= false and beneficiary_acc_number=?1 ", nativeQuery = true)
    Long sumOfTodaysExpenses(String accNumber);

//    @Query(
//            value = "WITH Dist AS (SELECT DISTINCT sender_account_number FROM validated_transactions) " +
//                    "SELECT  sender_account_number,SUM(transaction_amount) AS CommissionTotal " +
//                    "FROM validated_transactions WHERE reason='Referral Commission'" +
//                    " GROUP BY sender_account_number order by CommissionTotal desc",
//            nativeQuery = true)

//    @Query(
//            value = "select display_name, sum(validated_transactions.transaction_amount)" +
//                    " as Commission, count(sender_account_number) as referralsCount from customer \n" +
//                    "left join validated_transactions " +
//                    "on customer.account_number = validated_transactions.sender_account_number where reason='Referral Commission'\n" +
//                    "GROUP BY account_number",
//            nativeQuery = true
//    )


    //    select new TopPerfomers(display_name,sum(validated_transactions.transaction_amount)
//    as commissionTotal,count(sender_account_number) as referralsCount from customer) left join validated_transactions
//    on customer.account_number = validated_transactions.sender_account_number where reason='Referral Commission' GROUP BY account_number
//    @Query(
//                    "select new com.teleeza.wallet.teleeza.transactions.entity.TopPerfomers (display_name, sum(validated_transactions.transaction_amount)\" +\n" +
//                            "                            \" as Commission, count(sender_account_number) as referralsCount from customer \\n\" +\n" +
//                            "                            \"left join validated_transactions \" +\n" +
//                            "                            \"on customer.account_number = validated_transactions.sender_account_number where reason='Referral Commission'\\n\" +\n" +
//                            "                            \"GROUP BY account_number)"
//          )
//    @Query(value = "select display_name, sum(validated_transactions.transaction_amount) as Commission, count(sender_account_number) as referralsCount from customer \n" +
//            "left join validated_transactions on customer.account_number = validated_transactions.sender_account_number where reason='Referral Commission'\n" +
//            "GROUP BY account_number",
//    nativeQuery = true)
//    @Query(name = "topEarners", nativeQuery = true)
//    List<TopPerfomers> getTopEarners();

}

