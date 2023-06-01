package com.pg.paymentgateway.repository;

import com.pg.paymentgateway.model.BankStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface BankStatementRepository extends JpaRepository<BankStatement, Long>, JpaSpecificationExecutor<BankStatement> {

    public List<BankStatement> findByTransactionDate(Date date);

    public List<BankStatement> findByBankId(Integer bankId);

   /*@Query("SELECT t from bank_statement_record t " +
            //"WHERE t.transaction_date BETWEEN CURRENT_DATE AND :currentDateMinus2 " +
            "WHERE t.bank_id = :bankId " +
            "AND t.amount = :amount " + // add a space here
            "AND t.utr_number = :utrNum")*/
    List<BankStatement> findByBankIdAndAmountAndUtrNumber(
            @Param("bankId") int bankId,
            @Param("amount") String amount,
            @Param("utrNumber") String utrNumber);
    // @Param("currentDateMinus2") Date currentDateMinus2);

    @Query(value = "SELECT SUM(CAST(amount AS DECIMAL)) AS total_amount " +
            "FROM bank_statement_record " +
            "WHERE transaction_date = CURRENT_DATE AND account_id = :accountId", nativeQuery = true)
    Double getTotalAmountByAccountId(@Param("accountId") String accountId);

    List<BankStatement> findByCreatedAtGreaterThan(LocalDateTime dateTime);
}