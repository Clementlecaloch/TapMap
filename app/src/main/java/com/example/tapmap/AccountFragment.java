package com.example.tapmap;

import static com.example.tapmap.MainActivity.voyages;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> countryCodes = new ArrayList<>();
        for(Voyage v : voyages){
            for(Pin p : v.points){
                if(!countryCodes.contains(getCountryCode(p.longitude,p.latitude))){
                    countryCodes.add(getCountryCode(p.longitude,p.latitude));
                }
            }
        }

        ImageView lock1 = view.findViewById(R.id.account_lock1);
        ImageView lock2 = view.findViewById(R.id.account_lock2);
        ImageView lock3 = view.findViewById(R.id.account_lock3);

        if(voyages.size()>0){
            lock1.setVisibility(View.INVISIBLE);
        }

        if(countryCodes.size()>2){
            lock2.setVisibility(View.INVISIBLE);
        }
    }

    private String getCountryCode(double longitude, double latitude){
        LatLng coordinates = new LatLng(latitude,longitude); // Get the coordinates from your place
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses = null; // Only retrieve 1 address
        try {
            addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addresses.get(0);
        return address.getCountryCode();
    }
}