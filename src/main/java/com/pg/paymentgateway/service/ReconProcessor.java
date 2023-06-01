package com.pg.paymentgateway.service;

import com.pg.paymentgateway.model.BankStatement;
import com.pg.paymentgateway.model.Transaction;
import com.pg.paymentgateway.repository.BankStatementRepository;
import com.pg.paymentgateway.repository.TransactionRepository;
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

    private BankStatement bankStatement;

    private Map<String,BankStatement> statementCache = new HashMap<>();

    private Map<String,List<Transaction>> transCache = new HashMap<>();

    List<BankStatement> bankStatementsList = new LinkedList<>();

    List<Transaction> transactionsList = new LinkedList<>();


    public void initializeCache(){
        List<BankStatement> bankStatements = statementRepository.findByCreatedAtGreaterThan(LocalDateTime.now().minusDays(2));
        statementCache.clear();

        bankStatements.stream().forEach(bankStatement->{
            statementCache.put(bankStatement.getUtrNumber(), bankStatement);
        });

        List<Transaction> transactions = transactionRepository.findAll();

        transactions.stream().forEach(transaction -> {
            transCache.computeIfAbsent(transaction.getStatementTransactionNumber(),k -> new ArrayList<>()).add(transaction);
        });
        //Map<String, Transaction> transCache = transactions.stream().collect(Collectors.toMap(Transaction::getStatementTransactionNumber, Function.identity()));
    }

    private boolean isBankStatementExist(BankStatement fileBankStatement){
       /* List<BankStatement> bankStatementList = statementRepository.findByBankIdAndAmountAndUtrNumber(fileBankStatement.getBankId(), fileBankStatement.getAmount(), fileBankStatement.getUtrNumber());
        if(bankStatementList.isEmpty()) {
            return false;
        }*/
        BankStatement bankStatement1 = statementCache.get(fileBankStatement.getUtrNumber());
        if(bankStatement1 == null){
            return false;
        }
       // this.bankStatement = bankStatementList.get(0);
        this.bankStatement = bankStatement1;
        return true;
    }

    public BankStatement getBankStatement(){
        return this.bankStatement;
    }

    public void saveStatement(BankStatement statement){
        boolean isStatementAdded = false;
        try {
            if (!isBankStatementExist(statement)) {
                //statementRepository.save(statement);
                bankStatementsList.add(statement);
                isStatementAdded = true;
                this.bankStatement = statement;
            }
            if(this.bankStatement.getOrderId() == null){
                performRecon(isStatementAdded);
            }

        }catch (Exception e){
            logger.error("Error in saving and/or processing recon for bankId -" + this.bankStatement.getBankId() +
                    " UTR No: " + this.bankStatement.getUtrNumber() );
            logger.error(e.getMessage());
            throw e;
            //ToDO: Put these records in ErrorQueue to make it Fault tolerant
        }
    }

    private void performRecon(boolean isStatementInserted)
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
        //List<Transaction> transactions = transactionRepository.findByStatementTransactionNumber(this.bankStatement.getUtrNumber());
        List<Transaction> transactions = transCache.get(this.bankStatement.getUtrNumber());
        if(transactions != null){
            transactions.stream().forEach(transaction1 -> {
                if(Float.parseFloat(transaction1.getAmount()) == Float.parseFloat(this.bankStatement.getAmount())){
                    // Record Matched
                    this.bankStatement.setOrderId(transaction1.orderId);
                    this.bankStatement.setIsClaimed(1);
                    this.bankStatement.setUpdatedAt(LocalDateTime.now());
                    // Check for failed transition state and set flag
                    if("Failed".equals(transaction1.getStatus()) || ("Pending".equals(transaction1.getStatus()) && Instant.now().isAfter(transaction1.getStatusFailedAfter()))){
                        transaction1.setIsSuccessAfterFailed(1);
                    }
                    transaction1.setStatus("Success");
                    transaction1.setBankAccountId(this.bankStatement.getAccountId());
                    transactionsList.add(transaction1);
                    //transactionRepository.save(transaction1);
                }
            });
        }
        this.bankStatement.setIsChecked(1);
        if(!isStatementInserted){
            bankStatementsList.add(this.bankStatement);
        }
        //statementRepository.save(this.bankStatement);
        //getTransaction();
        //match statement and transaction
        //Update statement and transaction
    }

    public void commitRecords() {

        statementRepository.saveAllAndFlush(bankStatementsList);
        logger.info("Total statement records committed - " + bankStatementsList.size());
        transactionRepository.saveAllAndFlush(transactionsList);
        logger.info("Total transaction records committed - " + transactionsList.size());
    }
}
