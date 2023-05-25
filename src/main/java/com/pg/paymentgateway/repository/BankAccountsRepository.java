package com.pg.paymentgateway.repository;

import com.pg.paymentgateway.model.BankAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountsRepository extends JpaRepository<BankAccounts, Long>, JpaSpecificationExecutor<BankAccounts> {

    List<BankAccounts> findByAccountNumber(
                       @Param("accountNumber") String accountNumber);
}
