package com.teleeza.wallet.teleeza.location.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "locations")
@Data
public class Location {
    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "created_by")
    private Integer createdBy;
    @Column(name = "updated_by")
    private Integer updatedBy;
    @Column(name = "created_on")
//    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Column(name = "updated_on")
//    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;
    @Column(name = "county_no")
    private Integer countyNo;
    @Column(name = "flag")
    private String flag;
    @Column(name = "deleted_on")
    private Date deletedOn;
}
