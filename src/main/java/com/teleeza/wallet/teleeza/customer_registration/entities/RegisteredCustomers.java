package com.teleeza.wallet.teleeza.customer_registration.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "registered_customers")
public class RegisteredCustomers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_number")
    private String accNo;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "aml_score")
    private Long amlScore;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customerEntity", fetch = FetchType.LAZY)
//    @JsonManagedReference
//    private List<TransactionEntity> transactionEntity = new ArrayList<>();
}
