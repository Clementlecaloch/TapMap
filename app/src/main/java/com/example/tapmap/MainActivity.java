package com.example.tapmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    AddFragment addFragment = new AddFragment();
    FilterFragment filterFragment = new FilterFragment();
    ShareFragment shareFragment = new ShareFragment();
    AccountFragment accountFragment = new AccountFragment();
    MapFragment mapFragment = new MapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.map);
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