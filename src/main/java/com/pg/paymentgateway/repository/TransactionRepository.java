package com.pg.paymentgateway.repository;

import com.pg.paymentgateway.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    public List<Transaction> findByTxnId(String txnId);

    public List<Transaction> findByStatementTransactionNumber(String utrNo);

    Transaction findByTxnIdAndAmount(
            @Param("txnId") String txnId,
            @Param("amount") String amount);

}
