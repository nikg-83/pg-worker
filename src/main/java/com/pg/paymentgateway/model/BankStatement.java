package com.pg.paymentgateway.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="bank_statement_record")
public class BankStatement {

    @Id
    @SequenceGenerator(name="bank_statement_record_id_seq",
            sequenceName="bank_statement_record_id_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator="bank_statement_record_id_seq")
    @Column(name = "id", updatable=false)
    Long id;
    String transactionNumber;
    String amount;
    String utrNumber;
    Date transactionDate;
    String accountId;
    String accountName;
    Integer bankId;
    String orderId;
    Integer isClaimed;
    Integer isChecked;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
 //   String bankName;
//    String description;
//    String BranchName;
//    String DrAmount;
//    String Balance;
//    String KimsRemar;

}
