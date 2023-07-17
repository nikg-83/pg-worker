package com.pg.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RBLSmartFileProcessor implements FileProcessor{
    private static final Logger logger = LoggerFactory.getLogger(RBLSmartFileProcessor.class);
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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

    public void processMessage(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        ExcelDateUtil excelDateUtil = new ExcelDateUtil();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String accountNumber = null;
        Integer bankId = null;
        UUID uid = null;
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
                        statement.setTransactionDate(parseDate(row.get("TxnDate").asText(), sdf, row.toString()));
                        statement.setUtrNumber(matcher.group(1));

                        statement.setAccountId(row.get("AccNumber").asText());
                        if(bankId == null){
                            String accNum = row.get("AccNumber").asText();
                            List<BankAccounts> bankAccounts = bankAccountsRepository.findByAccountNumber(accNum);
                            if(CollectionUtils.isEmpty(bankAccounts)){
                                logger.error("Bank account is not configured -" + accNum);
                                return;
                            }
                            bankId = bankAccounts.get(0).getBankId();
                            accountNumber = accNum;
                        }
                        if(uid == null){
                            uid = UUID.fromString(row.get("UUID").asText());
                        }
                        statement.setUid(uid);
                        statement.setSeqNum(row.get("SeqNum").asInt());
                        statement.setBankId(bankId);
                        statement.setAccountName(row.get("AccName").asText());
                        statement.setIsClaimed(0);
                        statement.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
                        statement.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
                        bankStatementList.add(statement);
                    }
                }
            }
            reconProcessor.saveStatementList(bankStatementList);
            logger.info("RBL Smart message processing completed for AccId - " + accountNumber + " Total records are  " + totalrecordsCounter + " and total UPI records are " + upiCounter);
            if(upiCounter > 0){
                dailyLimitListener.handleEvent(new FileEvent(bankId, accountNumber));
            }

        } catch (JsonProcessingException e) {
            logger.error("Error in processing RBL Smart file records");
            throw new RuntimeException(e);
        }
    }

    private LocalDate parseDate(String date, DateTimeFormatter sdf, String record) {
        LocalDate sqldate = null;

        try {
            sqldate = LocalDate.parse(date, sdf);

        } catch (Exception e) {
            // Handle the parse exception
            logger.error("Unable to parse date -" + date + "for record - " + record);
            e.printStackTrace();
        } finally {
            return sqldate;
        }
    }

}
