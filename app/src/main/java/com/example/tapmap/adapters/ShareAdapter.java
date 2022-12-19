package com.example.tapmap.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapmap.R;
import com.example.tapmap.Voyage;

import java.util.ArrayList;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.MyViewHolder> {
    private final ArrayList<Voyage> listevoyages;


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textNom;
        CheckBox checkbox;
        View couleur;

        MyViewHolder(View view) {
            super(view);
            this.textNom = view.findViewById(R.id.textFilterAdapterNomVoyage);
            this.checkbox = view.findViewById(R.id.checkBoxFilterAdapter);
            this.couleur = view.findViewById(R.id.cardVoyage_couleur);
        }
    }

    public ShareAdapter(ArrayList<Voyage> liste) {
        this.listevoyages = liste;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_voyage, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TextView textNom = holder.textNom;
        CheckBox checkbox = holder.checkbox;
        View couleur = holder.couleur;

        textNom.setText(listevoyages.get(position).getNom());
        couleur.setBackgroundColor(listevoyages.get(position).getColor());
    }

    @Override
    public int getItemCount() {
        return listevoyages.size();
    }
}
