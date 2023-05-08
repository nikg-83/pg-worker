package com.pg.paymentgateway.repository;

import com.pg.paymentgateway.model.BankStatement;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BankStatementRepository extends CrudRepository<BankStatement, Long> {

    public List<BankStatement> findByTransactionDate(String date);
}