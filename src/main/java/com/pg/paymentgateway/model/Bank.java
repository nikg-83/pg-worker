package com.pg.paymentgateway.model;


import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name="banks")
public class Bank {

    @Id
    @SequenceGenerator(name="banks_id_seq",
            sequenceName="banks_id_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator="banks_id_seq")
    @Column(name = "id", updatable=false)
    Integer id;
    String bankName;
    String shortBankName;
    String bucket;
    String qrBucket;

}