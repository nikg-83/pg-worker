package com.pg.paymentgateway.service;

import com.pg.paymentgateway.data.OrderCallbackResponse;
import com.pg.paymentgateway.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class CallBackProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CallBackProcessor.class);

    @Autowired
    RestTemplate restTemplate;

     public OrderCallbackResponse createPayload(Transaction transaction ) {

        OrderCallbackResponse payload = new OrderCallbackResponse();
        payload.setOrder_id(transaction.getOrderId());
        payload.setCategory(transaction.getCategory());
        payload.setStatus(transaction.getStatus());
        payload.setAmount(transaction.getAmount());
        payload.set_claimed(true);
        payload.setRedirect_url(transaction.getRedirectUrl());
        payload.setCallback_url(transaction.getCallbackUrl());
        payload.setUtr(transaction.getStatementTransactionNumber());
        LocalDateTime ldt = LocalDateTime.ofInstant(transaction.getCreatedAt(), ZoneOffset.UTC);
        payload.setCreatedAt(ldt);
        ldt = LocalDateTime.ofInstant(transaction.getUpdatedAt(), ZoneOffset.UTC);
        payload.setUpdatedAt(ldt);
        return payload;
//        apiClient.sendOrderStatus(URI.create(transaction.getCallbackUrl()), payload);
    }

    public void sendCallBacks(List<Transaction> transactionsList){
        transactionsList.stream().forEach(transaction->{

            try {
                sendCallBackResponse(createPayload(transaction));
            } catch (URISyntaxException e) {
                logger.error("Callback failed for order id -" + transaction.getOrderId());
                //throw new RuntimeException(e);
            }
        });
    }

    private void sendCallBackResponse(OrderCallbackResponse payload) throws URISyntaxException {
        URI uri = new URI(payload.getCallback_url());
        restTemplate.postForEntity(uri, payload, OrderCallbackResponse.class);

    }
}
