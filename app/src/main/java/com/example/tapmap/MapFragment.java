package com.example.tapmap;

import static com.example.tapmap.MainActivity.voyages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import com.mapbox.maps.Style;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    MapView mapView = null;

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

        for(Voyage v : voyages) {
            if (v.filtered) {
                for (Pin p : v.points) {
                    createPointAnnotation(mapView, p);
                }
            }
        }
    }

    public void createPointAnnotation(MapView mapView, Pin pin) {
        AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(mapView);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);

        //conversion de l'objet pin en json element
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonElement jsonPin = gson.toJsonTree(pin);

        //paramétrage de l'annotation
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(Point.fromLngLat(pin.longitude, pin.latitude))
                .withData(jsonPin)
                .withIconImage(bm);

        //ajout de l'annotation à la carte
        pointAnnotationManager.create(pointAnnotationOptions);
    }
}