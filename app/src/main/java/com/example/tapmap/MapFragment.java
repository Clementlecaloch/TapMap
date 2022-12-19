package com.example.tapmap;

import static com.example.tapmap.MainActivity.SHARED_PREFERENCES_FILE;
import static com.example.tapmap.MainActivity.SHARED_PREFERENCES_VOYAGES;
import static com.example.tapmap.MainActivity.voyages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.ViewAnnotationAnchor;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.Cancelable;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import com.mapbox.maps.Style;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    MapView mapView = null;
    AnnotationPlugin annotationApi;
    PointAnnotationManager pointAnnotationManager;
    ActivityResultLauncher<Intent> launcherGetImage;

    TextView textNoImg;
    ImageView photo;

    boolean editing = false;
    boolean photoAdded = false;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcherGetImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData(); //on récupère les données qu'on a été chercher
                        try {
                            //on met notre image dans la vue de l'activité
                            InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            photo.setImageBitmap(bitmap);
                            textNoImg.setVisibility(View.GONE);
                            photoAdded=true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

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
                    if(p.newlyCreated<2){
                        Log.e("test","idid");
                        final Cancelable cancelable = CameraAnimationsUtils.flyTo(mapView.getMapboxMap(),
                                new CameraOptions.Builder()
                                        .center(Point.fromLngLat(p.longitude,p.latitude))
                                        .zoom(10.0)
                                        .build(),
                                new MapAnimationOptions.Builder().duration(4000).build());

                        createPointAnnotation(mapView, p, v.color, true);
                        p.newlyCreated++;
                    }else{
                        createPointAnnotation(mapView, p, v.color, false);
                    }
                }
            }
        }

    }

    public void createPointAnnotation(MapView mapView, Pin pin, int color, boolean newlycreated) {
        Bitmap bm = editBitmapColor(getBitmap(R.drawable.icon_pin),color);

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

        nom.setText(pin.nom);
        va.setOnClickListener(view -> showAlertDialogPin(pin));
        if(!newlycreated)
            va.setVisibility(View.GONE);
    }

    public void showAlertDialogPin(Pin p)
    {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_pin, null);
        builder.setView(customLayout);

        //infos du pin
        TextView lieu = customLayout.findViewById(R.id.dialogPinLieu);
        TextView description = customLayout.findViewById(R.id.dialogPinDescription);
        TextView voyage = customLayout.findViewById(R.id.dialogPinVoyage);
        photo = customLayout.findViewById(R.id.dialogPinPhoto);

        lieu.setText(p.nom);
        description.setText(p.description.equals("") ? "Aucune descrpition" : p.description);
        voyage.setText(p.voyage);
        if(p.isPhotoSet())
            photo.setImageBitmap(loadImage(p));
        else
            customLayout.findViewById(R.id.dialogPinNoPhoto).setVisibility(View.VISIBLE);

        //edit
        EditText editDescription = customLayout.findViewById(R.id.dialogPinEditDescription);
        ImageButton btnEdit = customLayout.findViewById(R.id.dialogPinbtnEdit);
        textNoImg = customLayout.findViewById(R.id.dialogPinNoPhoto);


        //Bouton pick image
        Button btnPickImage = customLayout.findViewById(R.id.dialogPinBtnBrowse);
        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  //on lance une activité qui va aller chercher une donnée
            intent.setType("image/*");  //on précise qu'on veut une image
            launcherGetImage.launch(intent);
        });

        btnEdit.setOnClickListener(v -> {
            if (editing) {
                p.description = editDescription.getText().toString();
                if(photoAdded) {
                    String nomImage = "image" + Math.random() * 100000;
                    saveImage(((BitmapDrawable) photo.getDrawable()).getBitmap(), nomImage);
                    p.photo = nomImage;
                }

                // save the task list to preference
                SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                try {
                    editor.putString(SHARED_PREFERENCES_VOYAGES, ObjectSerializer.serialize(voyages));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                editor.apply();

                description.setVisibility(View.VISIBLE);
                description.setText(p.description);
                editDescription.setVisibility(View.GONE);
                btnPickImage.setVisibility(View.GONE);
                btnEdit.setImageResource(R.drawable.icon_edit);
            } else {
                description.setVisibility(View.GONE);
                editDescription.setVisibility(View.VISIBLE);
                editDescription.setText(p.description);
                btnPickImage.setVisibility(View.VISIBLE);
                btnEdit.setImageResource(R.drawable.icon_save);
            }
            editing = !editing;
        });

        builder.setNegativeButton("Fermer", (dialogInterface, i) -> dialogInterface.cancel());

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void saveImage(Bitmap bitmapImage, String nom){
        //on trouve le chemin du répertoire privé de l'appli
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("photos", Context.MODE_PRIVATE);

        //on crée notre chemin en ajoutant le nom
        File mypath=new File(directory,nom);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // On compresse l'image en png
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos!=null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap loadImage(Pin p)
    {
        try {
            ContextWrapper cw = new ContextWrapper(getContext());
            File directory = cw.getDir("photos", Context.MODE_PRIVATE);
            File f = new File(directory, p.photo);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap editBitmapColor(Bitmap bm, int color){
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(bm);
        canvas.drawBitmap(bm, 0, 0, paint);

        return bm;
    }
}