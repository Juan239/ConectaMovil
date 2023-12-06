package com.example.examennacional.Controladores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examennacional.Modelo.contacto;
import com.example.examennacional.R;

import java.util.List;

public class contactosAdapter extends RecyclerView.Adapter<contactosAdapter.ViewHolder> {

    private List<contacto> contactoList;

    public contactosAdapter(List<contacto> contactoList) {
        this.contactoList = contactoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contactos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        contacto contacto = contactoList.get(position);

        holder.txtNombreContacto.setText(contacto.getNombreUsuario());
        holder.txtCorreoContacto.setText(contacto.getCorreoElectronico());
    }

    @Override
    public int getItemCount() {
        return contactoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreContacto;
        TextView txtCorreoContacto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreContacto = itemView.findViewById(R.id.txtNombreContacto);
            txtCorreoContacto = itemView.findViewById(R.id.txtCorreoContacto);
        }
    }
}
