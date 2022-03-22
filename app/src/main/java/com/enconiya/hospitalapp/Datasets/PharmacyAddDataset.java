package com.enconiya.hospitalapp.Datasets;

public class PharmacyAddDataset {
    String name;
    String lat;
    String lon;
    String opentime;
    String closetime;
    String addedbyname;
    String number;
    String image;

    public PharmacyAddDataset() {
    }

    public PharmacyAddDataset(String name, String lat, String lon, String opentime, String closetime, String addedbyname, String number, String image) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.opentime = opentime;
        this.closetime = closetime;
        this.addedbyname = addedbyname;
        this.number = number;
        this.image = image;
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

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getClosetime() {
        return closetime;
    }

    public void setClosetime(String closetime) {
        this.closetime = closetime;
    }

    public String getAddedbyname() {
        return addedbyname;
    }

    public void setAddedbyname(String addedbyname) {
        this.addedbyname = addedbyname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
