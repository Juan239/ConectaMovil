package com.example.examennacional.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.examennacional.Controladores.allContactosAdapter;
import com.example.examennacional.Controladores.contactosAdapter;
import com.example.examennacional.Modelo.contacto;
import com.example.examennacional.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class allContactosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewContactos;
    private allContactosAdapter contactoAdapter;
    private List<contacto> contactoList;
    private FirebaseDatabase database;
    private DatabaseReference referenceContacto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contactos);

        recyclerViewContactos = findViewById(R.id.recyclerViewAllContactos);

        // Obtén los datos de Firebase y configura el RecyclerView
        obtenerDatosFirebase();
    }

    private void obtenerDatosFirebase() {
        contactoList = new ArrayList<>();

        // Referencia al nodo "usuarios" en tu base de datos
        database = FirebaseDatabase.getInstance();
        referenceContacto = database.getReference("usuarios");

        // Añade un listener para obtener los datos
        referenceContacto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Itera sobre los nodos hijos dentro de "usuarios"
                for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                    String nombreUsuario = usuarioSnapshot.child("nombreUsuario").getValue(String.class);
                    String correoElectronico = usuarioSnapshot.child("correoElectronico").getValue(String.class);

                    contactoList.add(new contacto(nombreUsuario, correoElectronico));
                }

                // Configura el adaptador y el RecyclerView después de obtener los datos
                configurarRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja el error, si es necesario
            }
        });
    }

    private void configurarRecyclerView() {
        // Configura el adaptador y el RecyclerView
        contactoAdapter = new allContactosAdapter(contactoList);
        recyclerViewContactos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContactos.setAdapter(contactoAdapter);
    }
}