package com.pg.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.paymentgateway.model.BankAccounts;
import com.pg.paymentgateway.model.BankStatement;
import com.pg.paymentgateway.repository.BankAccountsRepository;
import com.pg.paymentgateway.util.ExcelDateUtil;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IDFCFileProcessor implements FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(IDFCFileProcessor.class);
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

    private Integer bankId;

    @Autowired
    BankAccountsRepository bankAccountsRepository;
    @Autowired
    ReconProcessor reconProcessor;

    @Autowired
    @Qualifier("dailyLimitListener")
    FileEventListener dailyLimitListener;
    private String accountNumber;

    public void processMessage(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            Pattern pattern = Pattern.compile("UPI/(\\w+)/(\\w+)/(.+)");
            int upiCounter = 0;
            int totalrecordsCounter = 0;
            List<BankStatement> bankStatementList = new ArrayList<>();
            for (JsonNode row : jsonNode) {
                totalrecordsCounter++;
                String description = row.get("Description").asText();
                if (StringUtils.hasLength(description)) {
                    logger.info(description);
                    Matcher matcher = pattern.matcher(description);
                    if (matcher.find()) {
                        upiCounter++;
                        val statement = new BankStatement();
                        statement.setAmount(row.get("Credit").asText());
                        statement.setTransactionDate(ExcelDateUtil.parseDate(row.get("TxnDate").asText(), sdf, "IDFC Bank"));
                        statement.setUtrNumber(matcher.group(2));
                        statement.setAccountId(row.get("AccNumber").asText());
                        if(bankId == null){
                            String accNum = row.get("AccNumber").asText();
                            List<BankAccounts> bankAccounts = bankAccountsRepository.findByAccountNumber(accNum);
                            bankId = bankAccounts.get(0).getBankId();
                            this.accountNumber = accNum;
                        }
                        statement.setBankId(bankId);
                        statement.setAccountName(row.get("AccName").asText());
                        statement.setIsClaimed(0);
                        statement.setCreatedAt(LocalDateTime.now());
                        statement.setUpdatedAt(LocalDateTime.now());
                        bankStatementList.add(statement);
                    }
                }

            }
            reconProcessor.saveStatementList(bankStatementList);
            logger.info("IDFC message processing completed for AccId - " + this.accountNumber + " Total records are  " + totalrecordsCounter + " and total UPI records are " + upiCounter);
            if(upiCounter > 0){
                dailyLimitListener.handleEvent(new FileEvent(bankId, accountNumber));
            }
        } catch (JsonProcessingException e) {
            logger.error("Error in processing IDFC file records");
            throw new RuntimeException(e);
        }
    }

    }
