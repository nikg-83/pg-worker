package com.pg.paymentgateway.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderCallbackResponse {

    Long _id;
    String order_id;
    String category;
    String idx;
    String setting;
    String device_type;
    String redirect_url;
    String callback_url;
    String utr;
    String amount;
    String status;
    boolean is_claimed;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
