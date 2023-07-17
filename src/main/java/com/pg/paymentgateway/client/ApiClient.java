package com.pg.paymentgateway.client;

import com.pg.paymentgateway.data.OrderCallbackResponse;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="pgclient")
public interface ApiClient {

    @RequestLine(value = "POST")
    String sendOrderStatus(OrderCallbackResponse orderCallbackResponse);

}