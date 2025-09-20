package com.teleeza.wallet.teleeza.rewarded_ads.merchant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "companies")
@Entity
public class Companies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column( name = "name", length = 100)
    private String name;

    @Column( name = "kra_pin", length = 100)
    private String kraPin;

    @Column( name = "business_id", length = 100)
    private String businessId;

    @Column( name = "email", length = 200)
    private String businessEmail;

    @Column( name = "business_activity", length = 255)
    private String businessActivity;

    @Column( name = "box_code", length = 100)
    private String boxCode;

    @Column( name = "city", length = 200)
    private String city;

    @Column( name = "physical_address", length = 255)
    private String physicalAddress;

    @Column( name = "region", length = 100)
    private String region;

    @Column( name = "county", length = 100)
    private String county;

    @Column( name="business_phone")
    private String businessPhone;

    @Column( name="business_phone2")
    private String businessPhone2;

    @Column( name="website")
    private String website;

    @Column( name="slogan")
    private String slogan;

    //Lazy Handler - Using this as long as there is one photo
    //Start:
    @Column( name = "logo_key")
    private String logoKey;

    @Column( name = "logo_url")
    private String logoUrl;

    @Column( name = "logo_checksum")
    private Long logoChecksum;
    //End:

    @Column( name = "location")
    private String location;

    @Column( name = "location_lat")
    private String locationLat;

    @Column( name = "location_lng")
    private String locationLng;

    @Column(name = "industry_no")
    private Long industryNo;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "facebook")
    private String facebook;


    @Column(name = "linkedin")
    private String linkedin;

    @Column( name = "flag")
    private String flag = "1";

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "target_group")
    private Boolean targetGroup = false;

//    @JoinColumn(name = "industry_no", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    private Industries industryLink;
//
//    @OneToOne(cascade = CascadeType.ALL, mappedBy = "companyLink", fetch = FetchType.LAZY)
//    private Partners partnerLink;
}
