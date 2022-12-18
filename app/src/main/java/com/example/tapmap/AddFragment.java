package com.example.tapmap;

import static com.example.tapmap.MainActivity.bottomNavigationView;
import static com.example.tapmap.MainActivity.voyages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

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
        btnCreerVoyage.setOnClickListener(view1 -> showAlertDialogButtonClicked());

        //Sauvegarde du pin
        editDescription = view.findViewById(R.id.editTextDescriptionPin);

        //bouton d'ajout
        btnAjouterPin=view.findViewById(R.id.btnAjouterPin);
        btnAjouterPin.setOnClickListener(v -> {
            if(selectedPlace != null) {
                if(spinnerVoyage.getSelectedItem() != null) {
                    Voyage selectedVoyage = new Voyage("Nouveau", false);
                    for (Voyage voy : voyages) {
                        if (voy.nom.equals(spinnerVoyage.getSelectedItem().toString()))
                            selectedVoyage = voy;
                    }
                    selectedVoyage.points.add(new Pin(selectedPlace, editDescription.getText().toString(), selectedVoyage.nom));
                    Toast.makeText(getContext(), "Point créé !",Toast.LENGTH_SHORT).show();

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

    public void showAlertDialogButtonClicked()
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
                    voyages.add(new Voyage(editText.getText().toString(), false));
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
}
