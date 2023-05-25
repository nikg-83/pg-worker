package com.pg.paymentgateway.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="bank_accounts_detail")
public class BankAccounts {

    @Id
    @SequenceGenerator(name="bank_accounts_detail_id_seq",
            sequenceName="bank_accounts_detail_id_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator="bank_accounts_detail_id_seq")
    @Column(name = "id", updatable=false)
    Long id;
    Integer bankId;
    String upiId;
    String accountNumber;
    String accountName;
    Double targetDaily;
    Double pendingDaily;
    String qrPath;
    String status;

}
