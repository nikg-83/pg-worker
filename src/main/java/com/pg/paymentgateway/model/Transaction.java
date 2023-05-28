package com.pg.paymentgateway.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;


@Data
@Entity
@Table(name="transactions")
public class Transaction {

    @Id
    public Long id;
    public String orderId;
    public String txnId;
    public String category;
    public String upiId;
    public String amount;
    public String discountedAmount;
    public String statementTransactionNumber;
    public String status;
    public String message;
    public String userUpiId;
    public String bankAccountId;
    public String deviceType;
    public String redirectUrl;
    public String callbackUrl;
    public long firstCallback;
    public long secondCallback;
    public long cron_1;
    public long cron_2;
    public long cron_3;
    public long cron_4;
    public long cron_5;
    public long cron_6;
    public int isSuccessAfterFailed;
    public long is_Failed_2;
    public long isUnclaimedCheck;
    public String note;
    public String systemName;
    public Instant createdAt;
    public Instant updatedAt;
    public Instant deletedAt;
    public Instant statusFailedAfter;
}
