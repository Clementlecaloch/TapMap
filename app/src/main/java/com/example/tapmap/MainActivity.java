package com.example.tapmap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
    static BottomNavigationView bottomNavigationView;

    static ArrayList<Voyage> voyages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //on met la map comme fragment par défaut
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, mapFragment).commit();

        // Initialise le Google Places SDK
        Places.initialize(getApplicationContext(), "AIzaSyBTUYBn93OPTOWEgA935T5vyOwHIPyh3Fw");
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