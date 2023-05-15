package com.pg.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.paymentgateway.model.BankStatement;
import com.pg.paymentgateway.util.ExcelDateUtil;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IDFCFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(IDFCFileProcessor.class);
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    @Autowired
    ReconProcessor reconProcessor;;

    public void processMessage(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
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
                        statement.setAmount(row.get("Credit").asText());
                        statement.setTransactionDate(ExcelDateUtil.parseDate(row.get("TxnDate").asText(), sdf, "IDFC Bank"));
                        statement.setUtrNumber(matcher.group(2));
                        statement.setBankId(3);
                        statement.setAccountId(row.get("AccNumber").asLong());
                        statement.setAccountName(row.get("AccName").asText());
                        statement.setIsClaimed(0);
                        statement.setCreatedAt(LocalDateTime.now());
                        statement.setUpdatedAt(LocalDateTime.now());
                        reconProcessor.saveStatement(statement);
                    }
                }


            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
