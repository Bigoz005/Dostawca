package com.example.dostawca.dto;

import java.io.Serializable;

public class Point implements Serializable {
    private String name;
    private String photoUrl;
    private String coordinates;

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

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Point(String name, String photoUrl, String coordinates) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.coordinates = coordinates;
    }
}
