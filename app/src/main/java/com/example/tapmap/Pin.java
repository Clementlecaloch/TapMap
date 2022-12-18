package com.example.tapmap;

import com.google.android.libraries.places.api.model.Place;

public class Pin {
    String nom;
    double longitude;

    double latitude;
    String description;
    String adresse;
    String voyage;

    public Pin(Place place, String description, String voyage){
        this.nom = place.getName();
        this.longitude = place.getLatLng().longitude;
        this.latitude = place.getLatLng().latitude;
        this.description = description;
        this.adresse = place.getAddress();
        this.voyage = voyage;
    }
}
