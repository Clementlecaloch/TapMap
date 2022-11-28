package com.example.tapmap;

import java.util.ArrayList;

public class Voyage {
    String nom;
    ArrayList<Pin> points = new ArrayList<>();

    boolean fini;

    public Voyage(String nom, boolean fini) {
        this.nom = nom;
        this.fini = fini;
    }
}
