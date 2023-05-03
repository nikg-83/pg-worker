package com.pg.paymentgateway.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name="bank_statement_record")
public class BankStatement {

    @Id
    Long id;
    String transactionNumber;
    String amount;
    String utrNumber;
    String transactionDate;
//    String description;
//    String BranchName;
//    String DrAmount;
//    String Balance;
//    String KimsRemar;

}
