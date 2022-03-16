package com.enconiya.hospitalapp.Datasets;

public class HospitalDataset {
    String name;
    String lat;
    String lon;
    String phone;
    String activedoctors;
    String beds;
    String grade;

    public HospitalDataset() {
    }

    public HospitalDataset(String name, String lat, String lon, String phone, String activedoctors, String beds, String grade) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.phone = phone;
        this.activedoctors = activedoctors;
        this.beds = beds;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActivedoctors() {
        return activedoctors;
    }

    public void setActivedoctors(String activedoctors) {
        this.activedoctors = activedoctors;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
