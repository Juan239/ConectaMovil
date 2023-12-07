package com.example.examennacional.Vista;

import com.example.examennacional.Modelo.usuario;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.examennacional.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class usuarioActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference referenceUsuario, referenceUsuario2;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private ImageView fotoPerfil;
    private EditText nuevoNombreUsuario;
    private Button btnGuardarDatos;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        fotoPerfil = findViewById(R.id.fotoPerfil);
        nuevoNombreUsuario = findViewById(R.id.ETNuevoNombreUsuario);
        btnGuardarDatos = findViewById(R.id.btnGuardarPerfil);

        // Obtén la instancia de FirebaseAuth y usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        referenceUsuario = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);
        referenceUsuario2 = database.getReference("usuarios").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference().child("images").child(userId);

        // Inicializar ActivityResultLauncher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleImageSelection(result.getData().getData());
                    }
                });

        if (currentUser != null) {
            obtenerNombreUsuarioActual(userId);
            checkIfImageExists(userId);

            // Abrir galería al presionar el imageView
            fotoPerfil.setOnClickListener(view -> openGallery());

            // Guardar la información
            btnGuardarDatos.setOnClickListener(view -> {
                subirImagenFirebase();
                actualizarNombreUsuario();
            });
        } else {
            startActivity(new Intent(usuarioActivity.this, listaContactosActivity.class));
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void handleImageSelection(Uri selectedImageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            fotoPerfil.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subirImagenFirebase() {
        // Tu código para subir la imagen a Firebase Storage
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String idUsuario = currentUser.getUid();
        // Obtener la imagen del ImageView

        BitmapDrawable drawable = (BitmapDrawable) fotoPerfil.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // Comprimir la imagen en un formato adecuado
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Crear una referencia en Firebase Storage


        // Subir la imagen a Firebase Storage
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // La imagen se ha subido exitosamente a Firebase Storage
                // Puedes obtener la URL de la imagen utilizando taskSnapshot.getDownloadUrl()
                // Aquí puedes agregar más lógica según tus necesidades

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Manejar errores durante la carga
                exception.printStackTrace();
            }
        });
    }

    private void actualizarNombreUsuario() {
        String nombreUsuario = nuevoNombreUsuario.getText().toString();

        // Verifica que el campo no esté vacío y no sea el valor predeterminado
        if (!nombreUsuario.isEmpty() && !nombreUsuario.equals("Nombre del Usuario")) {
            referenceUsuario.child("nombreUsuario").setValue(nombreUsuario)
                    .addOnSuccessListener(aVoid -> Toast.makeText(usuarioActivity.this, "Se guardaron los datos correctamente", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(usuarioActivity.this, "No se pudieron guardar los cambios", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(usuarioActivity.this, "Ingresa un nombre de usuario válido", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerNombreUsuarioActual(String userId) {
        referenceUsuario2 = database.getReference("usuarios").child(userId);

        referenceUsuario2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtén el nombreUsuario del usuario actual
                    String nombreUsuarioActual = snapshot.child("nombreUsuario").getValue(String.class);

                    if (nombreUsuarioActual != null) {
                        // Muestra el nombreUsuario en el EditText
                        nuevoNombreUsuario.setText(nombreUsuarioActual);
                        Log.d("UsuarioActivity", "Nombre de usuario obtenido exitosamente: " + nombreUsuarioActual);
                    } else {
                        Log.e("UsuarioActivity", "Nombre de usuario es nulo");
                        nuevoNombreUsuario.setText("Error al obtener el nombre de usuario");
                    }
                } else {
                    // El usuario no existe en la base de datos, maneja según tus necesidades
                    Log.e("UsuarioActivity", "El usuario no existe en la base de datos");
                    nuevoNombreUsuario.setText("Usuario no encontrado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja los errores si es necesario
                Log.e("UsuarioActivity", "Error en la lectura de datos: " + error.getMessage());
                nuevoNombreUsuario.setText("Error en la lectura de datos");
            }
        });
    }



    private void checkIfImageExists(String nombreImagenUsuario) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images").child(nombreImagenUsuario);


        // Verifica la existencia del archivo
        storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        //Agregar imagen al imageView
                        // Convierte los bytes en un Bitmap (o en el formato que necesites)
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        // Guarda el Bitmap en una variable global o según tus necesidades
                        // Puedes mostrar el Bitmap en una ImageView o realizar otras operaciones
                        // dependiendo de tus requerimientos.
                        // Por ejemplo, si necesitas mostrar la imagen en un ImageView:
                        ImageView imageView = findViewById(R.id.fotoPerfil);
                        imageView.setImageBitmap(bitmap);


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
