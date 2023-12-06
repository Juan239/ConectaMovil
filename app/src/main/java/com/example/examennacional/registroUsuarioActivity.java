package com.example.examennacional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examennacional.Modelo.contacto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registroUsuarioActivity extends AppCompatActivity {
    private TextView iniciarSesion;
    private EditText correo, contrasenia, nombreUsuario;
    private Button btnRegistrarse;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("usuarios");

        iniciarSesion = findViewById(R.id.txtIniciarSesion);
        correo = findViewById(R.id.ETcorreoR);
        contrasenia = findViewById(R.id.ETpasswordR);
        nombreUsuario = findViewById(R.id.ETnombreUsuarioR);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        mAuth = FirebaseAuth.getInstance();

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registroUsuarioActivity.this, MainActivity.class));
                finish();
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = correo.getText().toString();
                String pass = contrasenia.getText().toString();
                String username = nombreUsuario.getText().toString();

                contacto usuario = new contacto(username, email);
                reference.child(username).setValue(usuario);

                mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(registroUsuarioActivity.this, "Registro de usuario exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(registroUsuarioActivity.this, listaContactosActivity.class));
                        finish();
                    }
                });
            }
        });
    }
}