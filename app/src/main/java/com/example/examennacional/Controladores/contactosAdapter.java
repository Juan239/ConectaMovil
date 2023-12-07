package com.example.examennacional.Controladores;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.examennacional.chatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        private ImageView comenzarChat, eliminarContacto, fotoPerfil;
        private FirebaseDatabase database;
        private DatabaseReference referenceUsuarios, referenceContactos1, referenceContactos2;
        private FirebaseAuth mAuth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreContacto = itemView.findViewById(R.id.txtNombreContacto);
            //fotoPerfil = itemView.findViewById(R.id.foto_perfil_lista);
            comenzarChat = itemView.findViewById(R.id.iniciarChat);
            eliminarContacto = itemView.findViewById(R.id.eliminarContacto);
            comenzarChat = itemView.findViewById(R.id.iniciarChat);
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();


            eliminarContacto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtén el nombre del contacto a eliminar
                    String nombreContacto = txtNombreContacto.getText().toString();

                    if (!TextUtils.isEmpty(nombreContacto)) {
                        // Obtén la referencia al contacto específico y utiliza removeValue() para eliminarlo
                        referenceContactos1 = database.getReference().child("contactos").child(Objects.requireNonNull(mAuth.getUid())).child(nombreContacto);
                        referenceContactos1.removeValue();
                        Toast.makeText(itemView.getContext(), "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Muestra un mensaje indicando que el nombre del contacto no es válido
                        Toast.makeText(itemView.getContext(), "Selecciona un contacto válido", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            comenzarChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), chatActivity.class);
                    intent.putExtra("Nombre_receptor", txtNombreContacto.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });
        }


        public void bind(contacto contacto) {
            String nombreUsuario = contacto.getNombreUsuario();
            txtNombreContacto.setText(nombreUsuario);
            //colocarFotoPerfil(nombreUsuario);
        }
        /*
        public void colocarFotoPerfil(String nombreUsuario) {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference().child("images").child(mAuth.getUid());
            referenceUsuarios = database.getReference().child("usuarios").child(nombreUsuario).child("nombreUsuario");



            //final ImageView fotoPerfil = itemView.findViewById(R.id.foto_perfil_lista);
            if(nombreUsuario == referenceContactos1.toString()){
                storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        final long ONE_MEGABYTE = 1024 * 1024;
                        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Agregar imagen al imageView
                                // Convierte los bytes en un Bitmap (o en el formato que necesites)
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                // Guarda el Bitmap en una variable global o según tus necesidades
                                // Puedes mostrar el Bitmap en una ImageView o realizar otras operaciones
                                // dependiendo de tus requerimientos.
                                // Por ejemplo, si necesitas mostrar la imagen en un ImageView:

                                fotoPerfil.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Maneja la falla según tus necesidades
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // El archivo no existe
                        // Puedes manejar esta situación según tus necesidades
                    }
                });
        }
    }

         */
    }
}
