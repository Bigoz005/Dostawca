package com.example.dostawca.dao;

import com.example.dostawca.dto.Route;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDAO {
    private FirebaseDatabase firebaseDatabase;


    public FirebaseDAO() {
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    public void saveRouteForCurrentUser(Route route) {
        //todo: get current user ID from service
        firebaseDatabase.getReference().child("users").child("Du9Rue5ssZS4gYPmeUuL2rrKu4O2").setValue(route);
    }


}


