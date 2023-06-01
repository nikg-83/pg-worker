package com.pg.paymentgateway.service;

import com.pg.paymentgateway.model.BankStatement;
import com.pg.paymentgateway.model.Transaction;
import com.pg.paymentgateway.repository.BankStatementRepository;
import com.pg.paymentgateway.repository.TransactionRepository;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service

public class ReconProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ReconProcessor.class);
    @Autowired
    BankStatementRepository statementRepository;
    @Autowired
    TransactionRepository transactionRepository;

    private boolean isBankStatementExist(BankStatement fileBankStatement, Map<String,BankStatement> statementCache){

        BankStatement bankStatement1 = statementCache.get(fileBankStatement.getUtrNumber());
        if(bankStatement1 == null){
            return false;
        }

        return true;
    }

        public void saveStatementList(List<BankStatement> bankStatementList) {
            List<BankStatement> bankStatementsList = new LinkedList<>();
            List<Transaction> transactionsList = new LinkedList<>();
            Map<String,BankStatement> statementCache = new HashMap<>();
            Map<String,List<Transaction>> transCache = new HashMap<>();

            List<BankStatement> bankStatements = statementRepository.findByCreatedAtGreaterThan(LocalDateTime.now().minusDays(2));
            bankStatements.stream().forEach(bankStatement->{
                statementCache.put(bankStatement.getUtrNumber(), bankStatement);
            });

            List<Transaction> transactions = transactionRepository.findAll();
            transactions.stream().forEach(transaction -> {
                transCache.computeIfAbsent(transaction.getStatementTransactionNumber(),k -> new ArrayList<>()).add(transaction);
            });

            bankStatementList.stream().forEach(bankStatement -> {
                saveStatement(bankStatement, statementCache, transCache, bankStatementsList, transactionsList);
            });

            statementRepository.saveAllAndFlush(bankStatementsList);
            logger.info("Total statement records committed - " + bankStatementsList.size());
            transactionRepository.saveAllAndFlush(transactionsList);
            logger.info("Total transaction records committed - " + transactionsList.size());
        }

     public void saveStatement(BankStatement statement,
                               Map<String,BankStatement> statementCache,
                               Map<String,List<Transaction>> transCache,
                               List<BankStatement> bankStatementsList,
                               List<Transaction> transactionsList){
        boolean isStatementAdded = false;
        BankStatement bankStatement1;
        try {
            if (!isBankStatementExist(statement, statementCache)) {
                bankStatementsList.add(statement);
                isStatementAdded = true;
                bankStatement1 = statement;
            }else{
                bankStatement1 = statementCache.get(statement.getUtrNumber());
            }
            if(bankStatement1.getOrderId() == null){
                performRecon(bankStatement1,isStatementAdded, transCache, bankStatementsList, transactionsList);
            }

        }catch (Exception e){
            logger.error("Error in saving and/or processing recon for account -" + statement.getAccountId() +
                    " UTR No: " + statement.getUtrNumber() );
            logger.error(e.getMessage());
            throw e;
            //ToDO: Put these records in ErrorQueue to make it Fault tolerant
        }
    }

    private void performRecon(BankStatement newBankStatement,
                              boolean isStatementInserted,
                              Map<String,List<Transaction>> transCache,
                              List<BankStatement> bankStatementsList,
                              List<Transaction> transactionsList)
    {

        List<Transaction> transactions = transCache.get(newBankStatement.getUtrNumber());
        if(transactions != null){
            transactions.stream().forEach(transaction1 -> {
                if(Float.parseFloat(transaction1.getAmount()) == Float.parseFloat(newBankStatement.getAmount())){
                    // Record Matched
                    newBankStatement.setOrderId(transaction1.orderId);
                    newBankStatement.setIsClaimed(1);
                    newBankStatement.setUpdatedAt(LocalDateTime.now());
                    // Check for failed transition state and set flag
                    if("Failed".equals(transaction1.getStatus()) || ("Pending".equals(transaction1.getStatus()) && Instant.now().isAfter(transaction1.getStatusFailedAfter()))){
                        transaction1.setIsSuccessAfterFailed(1);
                    }
                    transaction1.setStatus("Success");
                    transaction1.setBankAccountId(newBankStatement.getAccountId());
                    transactionsList.add(transaction1);
                    //transactionRepository.save(transaction1);
                }
            });
        }
        newBankStatement.setIsChecked(1);
        if(!isStatementInserted){
            bankStatementsList.add(newBankStatement);
        }
    }

}
