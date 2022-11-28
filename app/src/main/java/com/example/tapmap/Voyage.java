package com.example.tapmap;

import com.example.tapmap.Pin;

import java.util.ArrayList;

public class Voyage {

    String nom;
    ArrayList<Pin> points = new ArrayList<>();

    boolean filtered = true;

    boolean fini;

    public Voyage(String nom, boolean fini) {
        this.nom = nom;
        this.fini = fini;
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

}
