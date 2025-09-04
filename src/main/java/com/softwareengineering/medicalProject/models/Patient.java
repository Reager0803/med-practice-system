package com.softwareengineering.medicalProject.models;

import jakarta.persistence.*;

@Entity
//@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String lastName;
    private String middleName;
    private String firstName;

    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private Long age;
//    in inches
    private Long height;
//    in pounds
    private Long weight;
//    name of insurance carrier
    private String insurance;
    private String primaryCareDoctor;

    public Patient() {}

    public Patient(Long id, String lastName, String middleName, String firstName, String address, String city, String state, String zip, String phone, Long age, Long height, Long weight, String insurance, String primaryCareDoctor) {
        this.id = id;
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.insurance = insurance;
        this.primaryCareDoctor = primaryCareDoctor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getPrimaryCareDoctor() {
        return primaryCareDoctor;
    }

    public void setPrimaryCareDoctor(String primaryCareDoctor) {
        this.primaryCareDoctor = primaryCareDoctor;
    }
}
