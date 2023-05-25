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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PNBFileProcessor implements FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PNBFileProcessor.class);
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    private Integer bankId;

    @Autowired
    BankAccountsRepository bankAccountsRepository;
    @Autowired
    ReconProcessor reconProcessor;
    @Autowired
    @Qualifier("dailyLimitListener")
    FileEventListener dailyLimitListener;
    public void processMessage(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
      //  List<BankStatement> bankStatementList = repository.findByTransactionDate(Date.valueOf(LocalDate.now()));
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            Pattern pattern = Pattern.compile("UPI/(\\w+)/(\\w+)/(.+)");
            for (JsonNode row : jsonNode) {
                String description = row.get("Description").asText();
                if (StringUtils.hasLength(description)) {
                    logger.info(description);
                    Matcher matcher = pattern.matcher(description);
                    if (matcher.find()) {
                        val statement = new BankStatement();
                        statement.setAmount(row.get("Cr Amount").asText());
                        statement.setTransactionDate(ExcelDateUtil.parseDate(row.get("Txn Date").asText(), sdf, "PNB Bank"));
                        statement.setUtrNumber(matcher.group(1));
                        statement.setAccountId(row.get("AccNum").asText());
                        if(bankId == null){
                            List<BankAccounts> bankAccounts = bankAccountsRepository.findByAccountNumber(row.get("AccNumber").asText());
                            bankId = bankAccounts.get(0).getBankId();
                        }
                        statement.setAccountName(row.get("AccName").asText());
                        statement.setIsClaimed(0);
                        statement.setCreatedAt(LocalDateTime.now());
                        statement.setUpdatedAt(LocalDateTime.now());
                        reconProcessor.saveStatement(statement);
                    }
                }


            }
            invokeEvents();
        } catch (JsonProcessingException e) {
            logger.error("Error in processing PNB file records");
            throw new RuntimeException(e);
        }

    }

    private void invokeEvents() {
        this.registerListener(dailyLimitListener);
        this.onFileComplete(new FileEvent(bankId, reconProcessor.getBankStatement().getAccountId()));
        this.unregisterListener(dailyLimitListener);
    }

}
