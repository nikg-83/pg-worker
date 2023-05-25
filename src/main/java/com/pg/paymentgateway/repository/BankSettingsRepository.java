package com.pg.paymentgateway.repository;

import com.pg.paymentgateway.model.BankSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankSettingsRepository extends JpaRepository<BankSettings, Long>, JpaSpecificationExecutor<BankSettings> {
        List<BankSettings> findByAccountIdAndStatus(
            @Param("accountId") Long accountId,
            @Param("status") String status);

    BankSettings findByAccountIdAndPriority(
            @Param("accountId") Long accountId,
            @Param("priority") int priority);

    List<BankSettings> findByPriorityGreaterThanAndCategoryOrderByPriority(
            @Param("priority") int priority,
            @Param("category") String category);
}
