package com.example.tapmap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    AddFragment addFragment = new AddFragment();
    FilterFragment filterFragment = new FilterFragment();
    ShareFragment shareFragment = new ShareFragment();
    AccountFragment accountFragment = new AccountFragment();
    MapFragment mapFragment = new MapFragment();

    static ArrayList<Voyage> voyages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (bottomNavigationView.getSelectedItemId() == 0) {
            bottomNavigationView.setSelectedItemId(R.id.map);
        }

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyBTUYBn93OPTOWEgA935T5vyOwHIPyh3Fw");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        voyages.add(new Voyage("Voyage sympa", false));
        voyages.get(0).points.add(new Pin("bshbs",0,0,"hshs","hsbh"));
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, mapFragment).commit();
                return true;
            case R.id.add:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, addFragment).commit();
                return true;
            case R.id.share:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, shareFragment).commit();
                return true;
            case R.id.account:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, accountFragment).commit();
                return true;
            case R.id.filter:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, filterFragment).commit();
                return true;
        }
        return false;
    }
}