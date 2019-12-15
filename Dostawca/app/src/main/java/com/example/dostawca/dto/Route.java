package com.example.dostawca.dto;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route implements Serializable {
    private String name;
    @ServerTimestamp
    Date created = new Date();
    private List<Point> points = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Route(String name, List<Point> points) {
        this.name = name;
        this.points = points;
    }

    public Route() {
    }
}
