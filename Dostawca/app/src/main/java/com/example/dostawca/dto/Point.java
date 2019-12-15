package com.example.dostawca.dto;

import java.io.Serializable;

public class Point implements Serializable {
    private String name;
    private String photoUrl;
    private String lat, lng;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Point() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public Point(String name, String photoUrl, String lat, String lng) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.lat = lat;
        this.lng = lng;
    }
}
