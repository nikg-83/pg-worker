package com.pg.paymentgateway.controller;

import com.pg.paymentgateway.service.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class TransactionController {
    @Autowired
    public PNBFileProcessor pnbFileProcessor;

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @GetMapping("/")
    public String saveTransactions() {
        logger.info("API call Received using daemon thread");
        return "Welcome to the ElasticBeanstalk SpringBoot example";
    }

    @PostMapping("/sqs")
    public ResponseEntity<Void> onSqsMessage(
            @RequestHeader(value = "User-Agent", required = false) String sqsdMessageUserAgent,
            @RequestHeader(value = "X-Aws-Sqsd-Msgid", required = false) String sqsdMessageId,
            @RequestHeader(value = "X-Aws-Sqsd-Queue", required = false) String sqsdMessageQueueName,
            @RequestHeader(value = "X-Aws-Sqsd-First-Received-At", required = false) String sqsdMessageReceivedTimestamp,
            @RequestHeader(value = "X-Aws-Sqsd-Receive-Count", required = false) Integer sqsdMessageCounts,
            @RequestHeader(value = "Content-Type", required = false) String sqsdMessageContentType,
            @RequestHeader(value = "X-Aws-Sqsd-Taskname", required = false) String sqsdMessagePeriodicTaskName,
            @RequestHeader(value = "X-Aws-Sqsd-Attr-(message-attribute-name)", required = false) String sqsdMessageCustomAttribute,
            @RequestHeader(value = "X-Aws-Sqsd-Scheduled-At", required = false) String sqsdMessageTaskSchdeuleTime,
            @RequestHeader(value = "X-Aws-Sqsd-Sender-Id", required = false) String sqsdMessageSenderId,
            @RequestBody String sqsdMessageBody) {

        logger.info("User-Agent: {}", sqsdMessageUserAgent);
        logger.info("Content-Type: {}", sqsdMessageContentType);
        logger.info("Message-Body: {}", sqsdMessageBody);
        logger.info("X-Aws-Sqsd-Msgid: {}", sqsdMessageId);
        logger.info("X-Aws-Sqsd-Taskname: {}", sqsdMessagePeriodicTaskName);
        logger.info("X-Aws-Sqsd-Queue: {}", sqsdMessageQueueName);
        logger.info("X-Aws-Sqsd-First-Received-At: {}", sqsdMessageReceivedTimestamp);
        logger.info("X-Aws-Sqsd-Receive-Count: {}", sqsdMessageCounts);
        logger.info("X-Aws-Sqsd-Attr-(message-attribute-name): {}", sqsdMessageCustomAttribute);
        logger.info("X-Aws-Sqsd-Scheduled-At: {}", sqsdMessageTaskSchdeuleTime);
        logger.info("X-Aws-Sqsd-Sender-Id: {}", sqsdMessageSenderId);

        pnbFileProcessor.processMessage(sqsdMessageBody);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
