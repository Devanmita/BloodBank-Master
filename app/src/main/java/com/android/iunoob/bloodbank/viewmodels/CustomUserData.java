package com.android.iunoob.bloodbank.viewmodels;

import java.io.Serializable;

public class CustomUserData implements Serializable {
    private String Address, Division, Contact;
    private String Name, BloodGroup;
    private String Time, Date;
    private String user_email; // Added user_email field

    public CustomUserData() {

    }

    public CustomUserData(String address, String division, String contact, String name, String bloodGroup, String time, String date, String user_email) {
        this.Address = address;
        this.Division = division;
        this.Contact = contact;
        this.Name = name;
        this.BloodGroup = bloodGroup;
        this.Time = time;
        this.Date = date;
        this.user_email = user_email; // Added to constructor
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        this.Division = division;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        this.Contact = contact;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
       this.Name = name;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.BloodGroup = bloodGroup;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    // Getter and Setter for user_email
    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
