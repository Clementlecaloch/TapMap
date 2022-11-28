package com.example.tapmap;

import static com.example.tapmap.MainActivity.voyages;

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

    Place selectedPlace;

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
        ArrayList<String> nomsVoyages = new ArrayList<>();
        for(Voyage v : voyages){
            nomsVoyages.add(v.nom);
        }

        String[] str = new String[nomsVoyages.size()];
        for (int i = 0; i < nomsVoyages.size(); i++) {
            str[i] = nomsVoyages.get(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, str);

        spinnerVoyage.setAdapter(adapter);


        //Sauvegarde du pin
        editDescription = view.findViewById(R.id.editTextDescriptionPin);

        btnAjouterPin=view.findViewById(R.id.btnAjouterPin);
        btnAjouterPin.setOnClickListener(v -> {
            Voyage selectedVoyage = new Voyage("Nouveau", false);
            for(Voyage voy : voyages){
               if (voy.nom.equals(spinnerVoyage.getSelectedItem().toString()))
                   selectedVoyage = voy;
            }
            selectedVoyage.points.add(new Pin(selectedPlace, editDescription.getText().toString()));
        });
    }
}
