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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class allContactosAdapter extends RecyclerView.Adapter<allContactosAdapter.ViewHolder> {
    private List<contacto> contactoList;

    public allContactosAdapter(List<contacto> contactoList) {
        this.contactoList = contactoList;
    }

    @NonNull
    @Override
    public allContactosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_contactos, parent, false);
        return new allContactosAdapter.ViewHolder(view);
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
        private TextView txtCorreoContacto;
        private ImageView btnAgregar;
        private FirebaseDatabase database;
        private DatabaseReference referenceNewContacto, referenceUsuarios, referenceId;
        private FirebaseAuth mAuth;

        private String nombreUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreContacto = itemView.findViewById(R.id.txtNombreContacto);
            txtCorreoContacto = itemView.findViewById(R.id.txtCorreoContacto);
            btnAgregar = itemView.findViewById(R.id.agregarContacto);
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();



            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtén la referencia al nodo "contactos" dentro del nodo con el mismo nombre que el UID del usuario actual
                    referenceNewContacto = database.getReference("contactos").child(mAuth.getUid());

                    final String nuevoContacto = txtNombreContacto.getText().toString();

                    // Verifica que el campo no esté vacío y no sea el valor predeterminado
                    if (!TextUtils.isEmpty(nuevoContacto) && !nuevoContacto.equals("Nombre del Contacto")) {
                        referenceNewContacto.child(nuevoContacto).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // El contacto no existe, añádelo al nodo
                                    referenceNewContacto.child(nuevoContacto).setValue(nuevoContacto);
                                    Toast.makeText(itemView.getContext(), "Contacto agregado exitosamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    // El contacto ya existe, muestra un mensaje o realiza otras acciones según tus necesidades
                                    Toast.makeText(itemView.getContext(), "El contacto ya existe", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Manejar errores si es necesario
                            }
                        });
                    } else {
                        // Muestra un mensaje indicando que el nombre del contacto no es válido
                        Toast.makeText(itemView.getContext(), "Ingresa un nombre de contacto válido", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        public void bind(contacto contacto) {
            txtNombreContacto.setText(contacto.getNombreUsuario());
            txtCorreoContacto.setText(contacto.getCorreoElectronico());
        }
    }
}
