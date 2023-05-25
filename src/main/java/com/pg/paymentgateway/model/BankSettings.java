package com.pg.paymentgateway.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="bank_settings")
public class BankSettings {

    @Id
    @SequenceGenerator(name="banks_id_seq",
            sequenceName="banks_id_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator="banks_id_seq")
    @Column(name = "id", updatable=false)
    Long id;
    String category;
    Long accountId;
    String status;
    Integer priority;

}

