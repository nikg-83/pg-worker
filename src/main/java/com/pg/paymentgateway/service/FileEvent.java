package com.pg.paymentgateway.service;

public class FileEvent {
    private Integer bankId;
    private String accountId;

    FileEvent(Integer bankId, String accountId){
        this.bankId = bankId;
        this.accountId = accountId;
    }

    public String getAccountId(){
        return this.accountId;
    }

    public Integer getBankId(){
        return this.bankId;
    }
}
