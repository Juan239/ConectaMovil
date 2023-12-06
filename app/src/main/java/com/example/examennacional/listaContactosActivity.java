package com.example.examennacional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.examennacional.Controladores.contactosAdapter;
import com.example.examennacional.Modelo.contacto;

import java.util.List;

public class listaContactosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewContactos;
    private contactosAdapter contactoAdapter;
    private List<contacto> contactoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        recyclerViewContactos = findViewById(R.id.recyclerViewContactos);
    }
}