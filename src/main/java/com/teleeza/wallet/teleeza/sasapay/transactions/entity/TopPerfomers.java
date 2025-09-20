package com.teleeza.wallet.teleeza.sasapay.transactions.entity;

import lombok.Data;


@Data
public class TopPerfomers {
    private String firstName;
    private String surName;

    private String referrals;
    private String todaysCount;
    private String photoUrl;

    public TopPerfomers(
            String firstName,
            String surName,
            String referrals,
            String photoUrl
    ) {
        this.firstName = firstName;
        this.surName = surName;
        this.referrals = referrals;
        this.photoUrl = photoUrl;
    }

    public TopPerfomers() {
    }
}
