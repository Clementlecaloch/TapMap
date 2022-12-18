package com.example.tapmap;

import static com.example.tapmap.MainActivity.SHARED_PREFERENCES_FILE;
import static com.example.tapmap.MainActivity.SHARED_PREFERENCES_VOYAGES;
import static com.example.tapmap.MainActivity.bottomNavigationView;
import static com.example.tapmap.MainActivity.voyages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.Hex;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    Button btnAjouterPin;
    Spinner spinnerVoyage;
    EditText editDescription;
    ArrayList<String> nomsVoyages = new ArrayList<>();
    ArrayAdapter<String> adapter;

    Place selectedPlace = null;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Fragment du lieu avec auto-complete
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                selectedPlace= place;
            }
            @Override
            public void onError(@NonNull Status status) {
            }
        });


        //Spinner pour choisir un voyage existant
        spinnerVoyage = view.findViewById(R.id.spinnerVoyage);
        nomsVoyages = getNomsSpinner();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nomsVoyages);
        spinnerVoyage.setAdapter(adapter);

        //Création de voyage
        Button btnCreerVoyage = view.findViewById(R.id.btnCreerVoyage);
        btnCreerVoyage.setOnClickListener(view1 -> dialogNewVoyage());

        //Sauvegarde du pin
        editDescription = view.findViewById(R.id.editTextDescriptionPin);

        //Choix d'une photo
        ImageView photo = view.findViewById(R.id.addPinImageView);
        TextView textNoImg = view.findViewById(R.id.addPinTextNoImage);

        ActivityResultLauncher<Intent> launcherGetImage = registerForActivityResult(
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
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

        //Bouton pick image
        ImageButton btnPickImage = view.findViewById(R.id.addPinPickImage);
        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  //on lance une activité qui va aller chercher une donnée
            intent.setType("image/*");  //on précise qu'on veut une image
            launcherGetImage.launch(intent);
        });

        //bouton d'ajout
        btnAjouterPin=view.findViewById(R.id.btnAjouterPin);
        btnAjouterPin.setOnClickListener(v -> {
            if(selectedPlace != null) {
                if(spinnerVoyage.getSelectedItem() != null) {
                    Voyage selectedVoyage = new Voyage("Nouveau");
                    for (Voyage voy : voyages) {
                        if (voy.nom.equals(spinnerVoyage.getSelectedItem().toString()))
                            selectedVoyage = voy;
                    }
                    String nomImage = "image" + Math.random()*100000;
                    saveImage(((BitmapDrawable) photo.getDrawable()).getBitmap(),nomImage);
                    selectedVoyage.points.add(new Pin(selectedPlace, editDescription.getText().toString(), selectedVoyage.nom, nomImage));
                    Toast.makeText(getContext(), "Point créé !",Toast.LENGTH_SHORT).show();

                    // save the task list to preference
                    SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    try {
                        Log.e("test",ObjectSerializer.serialize(voyages));
                        editor.putString(SHARED_PREFERENCES_VOYAGES, ObjectSerializer.serialize(voyages));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    editor.apply();

                    //on retourne sur la map
                    bottomNavigationView.setSelectedItemId(R.id.map);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flFragment, new MapFragment())
                            .addToBackStack(null)
                            .commit();
                } else
                    Toast.makeText(getContext(),"Veuillez selectionner un voyage", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(),"Veuillez choisir une adresse", Toast.LENGTH_SHORT).show();
        });
    }



    public void dialogNewVoyage()
    {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Créer un voyage");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_new_voyage, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("Créer", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            EditText editText = customLayout.findViewById(R.id.dialogVoyageNom);
            if(!editText.getText().toString().equals("")){
                boolean valid = true;
                for(Voyage v : voyages){
                    if(v.nom.equals(editText.getText().toString())){
                        Toast.makeText(getContext(),"Ce nom est déjà utilisé !", Toast.LENGTH_SHORT).show();
                        valid=false;
                        break;
                    }
                }
                if(valid) {
                    voyages.add(new Voyage(editText.getText().toString()));
                    adapter.add(editText.getText().toString());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("Annuler", (dialogInterface, i) -> dialogInterface.cancel());

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public ArrayList<String> getNomsSpinner(){
        ArrayList<String> voy = new ArrayList<>();
        for (Voyage v : voyages) {
            voy.add(v.nom);
        }
        return voy;
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
}
