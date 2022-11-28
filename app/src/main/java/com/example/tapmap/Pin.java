package com.example.tapmap;

import com.google.android.libraries.places.api.model.Place;

public class Pin {
    String nom;
    double longitude;
    double latitude;
    String description;
    String adresse;

    public Pin(String nom, double longitude, double latitude, String description, String adresse) {
        this.nom = nom;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.adresse = adresse;
    }

    public Pin(Place place, String description){
        this.nom = place.getName();
        this.longitude = place.getLatLng().longitude;
        this.latitude = place.getLatLng().latitude;
        this.description = description;
        this.adresse = place.getAddress();
    }
}
