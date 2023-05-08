package com.pg.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pg.paymentgateway.controller.TransactionController;
import com.pg.paymentgateway.model.BankStatement;
import com.pg.paymentgateway.repository.BankStatementRepository;
import com.pg.paymentgateway.util.ExcelDateUtil;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.*;

@Service
public class PNBFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PNBFileProcessor.class);
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    BankStatementRepository repository;
    public void processMessage(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        List<BankStatement> bankStatementList = repository.findByTransactionDate(LocalDate.now().toString());
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
                        statement.setAmount(row.get("CrAmount").asText());
                        statement.setTransactionDate(ExcelDateUtil.parseDate(row.get("TxnDate").asText(), sdf, "PNB Bank"));
                        statement.setUtrNumber(matcher.group(1));
                        repository.save(statement);
                    }
                }


            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
