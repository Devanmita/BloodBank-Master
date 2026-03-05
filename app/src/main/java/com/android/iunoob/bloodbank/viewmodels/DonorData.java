package com.android.iunoob.bloodbank.viewmodels;

public class DonorData {

    private String Name, Contact, Address;

    public DonorData() {

    }

    public DonorData(String name, String contact, String address) {
        this.Name = name;
        this.Contact = contact;
        this.Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        this.Contact = contact;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }
}
