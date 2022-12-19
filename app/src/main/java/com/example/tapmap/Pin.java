package com.example.tapmap;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;
import java.util.Date;

public class Pin implements Serializable {
    String nom;
    String description;
    String voyage;

    double longitude;
    double latitude;

    int newlyCreated = 0; //pour gérer le boing boing et le centrage de la cam à la création

    String photo = "no";

    Date date;

    public Pin(Place place, String description, String voyage, String photo){
        this.nom = place.getName();
        this.description = description;
        this.voyage = voyage;
        this.longitude = place.getLatLng().longitude;
        this.latitude = place.getLatLng().latitude;
        this.date = new Date();
        this.photo = photo;
    }

    public boolean isPhotoSet(){
        return !this.photo.equals("no");
    }

}
