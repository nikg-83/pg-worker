package com.pg.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.paymentgateway.model.Bank;
import com.pg.paymentgateway.model.BankAccounts;
import com.pg.paymentgateway.model.BankStatement;
import com.pg.paymentgateway.repository.BankAccountsRepository;
import com.pg.paymentgateway.repository.BankRepository;
import com.pg.paymentgateway.repository.BankStatementRepository;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RBLSmartFileProcessor implements FileProcessor{
    private static final Logger logger = LoggerFactory.getLogger(RBLSmartFileProcessor.class);
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private Integer bankId;

    @Autowired
    BankAccountsRepository bankAccountsRepository;

    @Autowired
    BankRepository bankRepository;
    @Autowired
    BankStatementRepository repository;
    @Autowired
    ReconProcessor reconProcessor;

    @Autowired
    @Qualifier("dailyLimitListener")
    FileEventListener dailyLimitListener;
    private String accountNumber;

    public void processMessage(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        ExcelDateUtil excelDateUtil = new ExcelDateUtil();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            Pattern pattern = Pattern.compile("UPI/(\\w+)/(.+)");
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
                        statement.setTransactionDate(excelDateUtil.parseDate(row.get("Value Date").asText(), sdf, "RBL SMart Bank"));
                        statement.setUtrNumber(matcher.group(1));

                        statement.setAccountId(row.get("AccNumber").asText());
                        if(bankId == null){
                            String accNum = row.get("AccNumber").asText();
                            List<BankAccounts> bankAccounts = bankAccountsRepository.findByAccountNumber(accNum);
                            this.accountNumber = accNum;
                            for(BankAccounts bankAccount : bankAccounts){
                                Optional<Bank> bank = bankRepository.findById(bankAccount.getBankId());
                                if(bank.isPresent() && "RBL Smart Bank".equals(bank.get().getBankName())){
                                    bankId = bankAccount.getBankId();
                                }
                            }

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
            logger.info("RBL Smart message processing completed for AccId - " + this.accountNumber + " Total records are  " + totalrecordsCounter + " and total UPI records are " + upiCounter);
            if(upiCounter > 0){
                dailyLimitListener.handleEvent(new FileEvent(bankId, accountNumber));
            }

        } catch (JsonProcessingException e) {
            logger.error("Error in processing RBL Smart file records");
            throw new RuntimeException(e);
        }
    }

}
