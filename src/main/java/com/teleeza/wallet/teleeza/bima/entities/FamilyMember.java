package com.teleeza.wallet.teleeza.bima.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;


import javax.persistence.*;

@Entity
@Table(name = "family_members")
//@Data
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String relationship;
    private String mobileNumber;
    private String gender;
    private String dateOfBirth;
    private String email;
    private Integer age;
    private String principalPhoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public FamilyMember() {
    }

    public FamilyMember(Long id, String fullName, String relationship, String mobileNumber, String gender,
                        String dateOfBirth, String email, Integer age, String principalPhoneNumber, Customer customer) {
        this.id = id;
        this.fullName = fullName;
        this.relationship = relationship;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.age = age;
        this.principalPhoneNumber = principalPhoneNumber;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPrincipalPhoneNumber() {
        return principalPhoneNumber;
    }

    public void setPrincipalPhoneNumber(String principalPhoneNumber) {
        this.principalPhoneNumber = principalPhoneNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "FamilyMember{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", relationship='" + relationship + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", principalPhoneNumber='" + principalPhoneNumber + '\'' +
//                ", customer=" + customer +
                '}';
    }
}
