package com.enconiya.hospitalapp.Datasets;

public class UserDataset {
    String name;
    String mobile;
    String email;
    String date;

    public UserDataset(String name, String mobile, String email, String date) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.date = date;
    }

    public UserDataset() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
