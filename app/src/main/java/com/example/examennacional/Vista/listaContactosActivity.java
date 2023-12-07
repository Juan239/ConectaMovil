package com.example.examennacional.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examennacional.Controladores.contactosAdapter;
import com.example.examennacional.Modelo.contacto;
import com.example.examennacional.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class listaContactosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewContactos;
    private contactosAdapter contactoAdapter;
    private List<contacto> contactoList;
    private FirebaseDatabase database;
    private DatabaseReference referenceContacto, referenceUsuarios;
    private FloatingActionButton btnAgregarContacto, btnEditarPerfil;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        recyclerViewContactos = findViewById(R.id.recyclerViewContactos);
        btnAgregarContacto = findViewById(R.id.btnAgregarContacto);
        btnEditarPerfil = findViewById(R.id.btnEditarUsuario);



        // Obtén los datos de Firebase y configura el RecyclerView
        obtenerDatosFirebase();

        btnAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(listaContactosActivity.this, allContactosActivity.class));
            }
        });

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(listaContactosActivity.this, usuarioActivity.class));
            }
        });

    }


    private void obtenerDatosFirebase() {
        contactoList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        // Referencia al nodo "usuarios" en tu base de datos
        database = FirebaseDatabase.getInstance();
        referenceContacto = database.getReference("contactos").child(Objects.requireNonNull(mAuth.getUid()));
        referenceUsuarios = database.getReference("usuarios");

        // Añade un listener para obtener los datos
        referenceContacto.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactoList.clear();
                // Itera sobre los nodos hijos dentro de "contactos"
                for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                    String nombreUsuario = usuarioSnapshot.getKey();

                    contactoList.add(new contacto(nombreUsuario));
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
        contactoAdapter = new contactosAdapter(contactoList);
        recyclerViewContactos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContactos.setAdapter(contactoAdapter);
    }
}
