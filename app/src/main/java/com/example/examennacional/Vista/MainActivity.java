package com.example.examennacional.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examennacional.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView registrarse;
    private EditText correo, contrasenia;
    private Button btnIniciarSesion;
    private CheckBox mantenerSesionIniciada;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrarse = findViewById(R.id.txtRegistrarse);
        correo = findViewById(R.id.ETcorreo);
        contrasenia = findViewById(R.id.ETpassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        mantenerSesionIniciada = findViewById(R.id.mantenerSesion);

        mAuth = FirebaseAuth.getInstance();

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, registroUsuarioActivity.class));
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = correo.getText().toString();
                String pass = contrasenia.getText().toString();
                mAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(MainActivity.this, listaContactosActivity.class));
                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}