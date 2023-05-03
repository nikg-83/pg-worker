package com.pg.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pg.paymentgateway.controller.TransactionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.*;

@Service
public class PNBFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PNBFileProcessor.class);

    public static void processMessage(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            Pattern pattern = Pattern.compile("UPI/(\\w+)/(\\w+)/(\\w+)/(.+)/");
            for (JsonNode row : jsonNode) {
                String description = row.get("Description").toString();
                if (StringUtils.hasLength(description)){
                    Matcher matcher = pattern.matcher(description);
                    if(matcher.matches()){
                        logger.info("UPI Id -" + matcher.group(1));
                        logger.info("Txn No - " + row.get("TxnNo"));
                        logger.info("Cr Amount - " + row.get("CrAmount"));
                        logger.info("Txn Date - " + row.get("TxnDate"));
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
