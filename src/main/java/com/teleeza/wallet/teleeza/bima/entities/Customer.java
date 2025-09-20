package com.teleeza.wallet.teleeza.bima.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "customers")

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String mobileNumber;
    private String address;
    private String gender;
    private String dateOfBirth;
    private String email;
    private String idNumber;
    private String documentType;

    private String maritalStatus;
    @JsonManagedReference
    @OneToOne(mappedBy = "customer",cascade = CascadeType.ALL,orphanRemoval = true)
    private NextOfKin nextOfKin;
    @JsonManagedReference
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<FamilyMember> familyMembers;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "policy_id")
    private Integer policyId;
    @Column(name = "total_payments")
    private BigDecimal totalPayments;
    @Column(name = "start_date")
    private String startDate;
    @Column(name = "end_date")
    private String endDate;

    public Customer() {
    }

    public Customer(Long id, String fullName, String mobileNumber, String address, String gender, String dateOfBirth,
                    String email, String idNumber, String documentType, String maritalStatus, NextOfKin nextOfKin, List<FamilyMember> familyMembers, String policyNumber, Integer policyId, BigDecimal totalPayments, String startDate, String endDate) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.idNumber = idNumber;
        this.documentType = documentType;
        this.maritalStatus = maritalStatus;
        this.nextOfKin = nextOfKin;
        this.familyMembers = familyMembers;
        this.policyNumber = policyNumber;
        this.policyId = policyId;
        this.totalPayments = totalPayments;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public NextOfKin getNextOfKin() {
        return nextOfKin;
    }


    public void setNextOfKin(NextOfKin nextOfKin) {
        this.nextOfKin = nextOfKin;
    }

    public List<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public BigDecimal getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(BigDecimal totalPayments) {
        this.totalPayments = totalPayments;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", email='" + email + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", documentType='" + documentType + '\'' +
                ", policyNumber'" + policyNumber + '\'' +
                ", policyId'" + policyId +'\'' +
//                ", nextOfKin=" + nextOfKin +
//                ", familyMembers=" + familyMembers +
                '}';
    }
}