package com.pg.paymentgateway.repository;

import com.pg.paymentgateway.model.BankStatement;
import org.springframework.data.repository.CrudRepository;

public interface BankStatementRepository extends CrudRepository<BankStatement, Long> {

}
