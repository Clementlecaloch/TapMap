package com.example.tapmap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    MapView mapView = null;
    ViewAnnotationManager vam;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS);


        createCircleAnnotation(mapView, Point.fromLngLat(0,0));
    }

    public void createCircleAnnotation(MapView mapView, Point point) {

        AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(mapView);
        CircleAnnotationManager circleAnnotationManager = CircleAnnotationManagerKt.createCircleAnnotationManager(annotationApi, new AnnotationConfig());

        // circle annotations options
        CircleAnnotationOptions circleAnnotationOptions = new CircleAnnotationOptions()
                .withPoint(point)
                .withCircleRadius(8.0)
                .withCircleColor("#ee4e8b")
                .withCircleBlur(0.5)
                .withCircleStrokeWidth(2.0)
                .withCircleStrokeColor("#ffffff");

        circleAnnotationManager.create(circleAnnotationOptions);
    }


}