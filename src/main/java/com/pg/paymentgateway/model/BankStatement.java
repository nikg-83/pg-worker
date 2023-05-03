package com.pg.paymentgateway.model;

import lombok.Data;

@Data
public class BankStatement {

    String TxnNo;
    String TxnDate;
    String Description;
    String BranchName;
    String DrAmount;
    String CrAmount;
    String Balance;
    String KimsRemar;

}
