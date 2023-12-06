package com.example.examennacional.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.examennacional.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class usuarioActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference referenceUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
    }
}