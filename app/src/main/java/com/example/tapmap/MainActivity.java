package com.example.tapmap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationManager;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

public class MainActivity extends AppCompatActivity {

    MapView mapView = null;
    ViewAnnotationManager vam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS);
        mapView.getViewAnnotationManager();
    }
}