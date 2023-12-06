package com.example.examennacional.Controladores;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examennacional.Modelo.contacto;
import com.example.examennacional.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

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
        holder.bind(contacto);
    }

    @Override
    public int getItemCount() {
        return contactoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombreContacto;
        private ImageView comenzarChat, eliminarContacto;
        private FirebaseDatabase database;
        private DatabaseReference reference;
        private FirebaseAuth mAuth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreContacto = itemView.findViewById(R.id.txtNombreContacto);
            comenzarChat = itemView.findViewById(R.id.iniciarChat);
            eliminarContacto = itemView.findViewById(R.id.eliminarContacto);
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();

            eliminarContacto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtén el nombre del contacto a eliminar
                    String nombreContacto = txtNombreContacto.getText().toString();

                    if (!TextUtils.isEmpty(nombreContacto)) {
                        // Obtén la referencia al contacto específico y utiliza removeValue() para eliminarlo
                        DatabaseReference reference = database.getReference().child("contactos").child(Objects.requireNonNull(mAuth.getUid())).child(nombreContacto);
                        reference.removeValue();
                        Toast.makeText(itemView.getContext(), "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Muestra un mensaje indicando que el nombre del contacto no es válido
                        Toast.makeText(itemView.getContext(), "Selecciona un contacto válido", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        public void bind(contacto contacto) {
            txtNombreContacto.setText(contacto.getNombreUsuario());

        }
    }
}
