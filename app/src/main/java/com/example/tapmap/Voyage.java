package com.example.tapmap;

import com.example.tapmap.Pin;

import java.io.Serializable;
import java.util.ArrayList;

public class Voyage implements Serializable {

    String nom;
    ArrayList<Pin> points = new ArrayList<>();
    int color;

    boolean filtered = true;

    public Voyage(String nom, int color) {
        this.nom = nom;
        this.color = color;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public String getNom() {
        return nom;
    }

    public int getColor() {
        return color;
    }

}
