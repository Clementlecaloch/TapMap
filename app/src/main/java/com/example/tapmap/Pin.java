package com.example.tapmap;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;
import java.util.Date;

public class Pin implements Serializable {
    String nom;
    double longitude;

    double latitude;
    String description;
    String adresse;
    String voyage;

    String photo = "no";

    Date date;

    public Pin(Place place, String description, String voyage, String photo){
        this.nom = place.getName();
        this.longitude = place.getLatLng().longitude;
        this.latitude = place.getLatLng().latitude;
        this.description = description;
        this.adresse = place.getAddress();
        this.voyage = voyage;
        this.date = new Date();
        this.photo = photo;
    }

    public boolean isPhotoSet(){
        return !this.photo.equals("no");
    }
}
