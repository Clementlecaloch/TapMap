package com.example.tapmap;

import static com.example.tapmap.MainActivity.voyages;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.RenderedQueryGeometry;
import com.mapbox.maps.RenderedQueryOptions;
import com.mapbox.maps.ViewAnnotationAnchor;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.plugin.annotation.Annotation;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    MapView mapView = null;
    AnnotationPlugin annotationApi;
    PointAnnotationManager pointAnnotationManager;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS);

        annotationApi = AnnotationPluginImplKt.getAnnotations(mapView);
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());
        pointAnnotationManager.addClickListener(pointAnnotation -> {
            ViewAnnotationManager vam = mapView.getViewAnnotationManager();
            View va = vam.getViewAnnotationByFeatureId(pointAnnotation.getFeatureIdentifier());
            if (va.getVisibility() == View.GONE) {
                va.setVisibility(View.VISIBLE);
            } else {
                va.setVisibility(View.GONE);
            }
            return false;
        });

        for(Voyage v : voyages) {
            if (v.filtered) {
                for (Pin p : v.points) {
                    createPointAnnotation(mapView, p);
                }
            }
        }

    }

    public void createPointAnnotation(MapView mapView, Pin pin) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);

        //conversion de l'objet pin en json element
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonElement jsonPin = gson.toJsonTree(pin);

        //paramétrage de l'annotation
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(Point.fromLngLat(pin.longitude, pin.latitude))
                .withData(jsonPin)
                .withIconImage(bm)
                .withIconAnchor(IconAnchor.BOTTOM);

        //ajout de l'annotation à la carte
        PointAnnotation pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions);

        ViewAnnotationManager vam = mapView.getViewAnnotationManager();
        View va = vam.addViewAnnotation(
                R.layout.view_annotation,
                new ViewAnnotationOptions.Builder()
                        .geometry(Point.fromLngLat(pin.longitude, pin.latitude))
                        .associatedFeatureId(pointAnnotation.getFeatureIdentifier())
                        .anchor(ViewAnnotationAnchor.BOTTOM)
                        .offsetY(pointAnnotation.getIconImageBitmap().getHeight())
                        .build()
                );
        TextView nom = va.findViewById(R.id.viewAnnotationNom);
        TextView description = va.findViewById(R.id.viewAnnotationDescription);

        nom.setText(pin.nom);
        description.setText(pin.description);
        va.setOnClickListener(view -> showAlertDialogPin(pin));
        va.setVisibility(View.GONE);
    }

    public void showAlertDialogPin(Pin p)
    {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pin");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_pin, null);
        builder.setView(customLayout);

        TextView lieu = customLayout.findViewById(R.id.dialogPinLieu);
        TextView description = customLayout.findViewById(R.id.dialogPinDescription);
        TextView voyage = customLayout.findViewById(R.id.dialogPinVoyage);
        Button btnDelete = customLayout.findViewById(R.id.dialogPinDelete);

        lieu.setText(p.nom);
        description.setText(p.description);
        voyage.setText(p.voyage);

        builder.setNegativeButton("Fermer", (dialogInterface, i) -> dialogInterface.cancel());

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}