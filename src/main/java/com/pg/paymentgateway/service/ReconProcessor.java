package com.pg.paymentgateway.service;

import com.pg.paymentgateway.model.BankStatement;
import com.pg.paymentgateway.model.Transaction;
import com.pg.paymentgateway.repository.BankStatementRepository;
import com.pg.paymentgateway.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class ReconProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ReconProcessor.class);
    @Autowired
    BankStatementRepository statementRepository;
    @Autowired
    TransactionRepository transactionRepository;

    BankStatement bankStatement;

    private boolean isBankStatementExist(BankStatement fileBankStatement){
        List<BankStatement> bankStatementList = statementRepository.findByBankIdAndAmountAndUtrNumber(fileBankStatement.getBankId(), fileBankStatement.getAmount(), fileBankStatement.getUtrNumber());
        if(bankStatementList.isEmpty()) {
            return false;
        }
        this.bankStatement = bankStatementList.get(0);
        return true;
    }

    public void saveStatement(BankStatement statement){
        try {
            if (!isBankStatementExist(statement)) {
                statementRepository.save(statement);
                this.bankStatement = statement;
            }
            if(this.bankStatement.getOrderId() == null){
                performRecon();
            }

        }catch (Exception e){
            logger.error("Error in saving and/or processing recon for bankId -" + this.bankStatement.getBankId() +
                    " UTR No: " + this.bankStatement.getUtrNumber() );
            logger.error(e.getMessage());
            throw e;
            //ToDO: Put these records in ErrorQueue to make it Fault tolerant
        }
    }

    private void performRecon()
    {
       /* Transaction transaction = transactionRepository.findByTxnIdAndAmount(this.bankStatement.getUtrNumber(),this.bankStatement.getAmount());
        if(transaction != null){
            this.bankStatement.setOrderId(transaction.orderId);
            this.bankStatement.setIsClaimed(1);
            transaction.setStatus("Success");
            transaction.setBankAccountId(Long.toString(this.bankStatement.getAccountId()));
            transactionRepository.save(transaction);
        }*/
        //List<Transaction> transactions = transactionRepository.findAll();
        //Map<String, Transaction> transCache = transactions.stream().collect(Collectors.toMap(Transaction::getTxnId, Function.identity()));
        List<Transaction> transactions = transactionRepository.findByTxnId(this.bankStatement.getUtrNumber());
        transactions.stream().forEach(transaction1 -> {
            if(Float.parseFloat(transaction1.getAmount()) == Float.parseFloat(this.bankStatement.getAmount())){
                // Record Matched
                this.bankStatement.setOrderId(transaction1.orderId);
                this.bankStatement.setIsClaimed(1);
                transaction1.setStatus("Success");
                transaction1.setBankAccountId(Long.toString(this.bankStatement.getAccountId()));
                transactionRepository.save(transaction1);
                }
            });

        this.bankStatement.setIsChecked(1);
        statementRepository.save(this.bankStatement);
        //getTransaction();
        //match statement and transaction
        //Update statement and transaction
    }

    private Optional<BankStatement> getTransactions(){

           return null;
    }

}
