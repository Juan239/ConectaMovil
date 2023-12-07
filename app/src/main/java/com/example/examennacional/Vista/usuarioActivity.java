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
    private DatabaseReference referenceUsuario, referencetelefono, reference2;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private ImageView fotoPerfil;
    private EditText nuevoTelefono;
    private Button btnGuardarDatos;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        fotoPerfil = findViewById(R.id.fotoPerfil);
        nuevoTelefono = findViewById(R.id.ETNuevoTelefono);
        btnGuardarDatos = findViewById(R.id.btnGuardarPerfil);

        // Obtén la instancia de FirebaseAuth y usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            String userId = currentUser.getUid();
            referenceUsuario = database.getReference("IdUsuarios");

            // Inicializar ActivityResultLauncher
            galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            handleImageSelection(result.getData().getData());
                        }
                    });

            obtenerDatosUsuario(userId);
            checkIfImageExists(userId);

            // Abrir galería al presionar el imageView
            fotoPerfil.setOnClickListener(view -> openGallery());

            // Guardar la información
            btnGuardarDatos.setOnClickListener(view -> {
                subirImagenFirebase(userId);
                actualizarNumeroUsuario(userId);
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

    private void subirImagenFirebase(String userId) {
        // Tu código para subir la imagen a Firebase Storage
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String idCorreo = currentUser.getUid();
        // Obtener la imagen del ImageView

        BitmapDrawable drawable = (BitmapDrawable) fotoPerfil.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // Comprimir la imagen en un formato adecuado
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Crear una referencia en Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference().child("images").child(userId);

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

    private void actualizarNumeroUsuario(String userId) {
        String nuevoNumeroUsuario = nuevoTelefono.getText().toString();

        // Verifica que el campo no esté vacío y no sea el valor predeterminado
        if (!nuevoNumeroUsuario.isEmpty() && !nuevoNumeroUsuario.equals("Teléfono del Usuario")) {
            DatabaseReference referenceIdUsuarios = database.getReference().child("IdUsuarios");

            // Obtén la referencia al nombre de usuario usando el UID
            referenceIdUsuarios.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Obtiene el nombre de usuario asociado al UID
                        String nombreUsuario = snapshot.getValue(String.class);

                        if (nombreUsuario != null) {
                            // Ahora, actualiza el valor del teléfono en el nodo "usuarios" usando el nombre de usuario
                            DatabaseReference referenceUsuarios = database.getReference().child("usuarios");
                            DatabaseReference referenciaUsuarioActual = referenceUsuarios.child(nombreUsuario);

                            referenciaUsuarioActual.child("telefono").setValue(nuevoNumeroUsuario)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(usuarioActivity.this, "Se actualizó el número de teléfono correctamente", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(usuarioActivity.this, "No se pudo actualizar el número de teléfono", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(usuarioActivity.this, "No se encontró el nombre de usuario asociado al UID", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Maneja los errores si es necesario
                    Log.e("UsuarioActivity", "Error en la lectura de datos: " + error.getMessage());
                }
            });
        } else {
            Toast.makeText(usuarioActivity.this, "Ingresa un número de teléfono válido", Toast.LENGTH_SHORT).show();
        }
    }
    private void obtenerDatosUsuario(String userId) {

        referencetelefono = database.getReference().child("usuarios");

        DatabaseReference referenceIdUsuarios = database.getReference().child("IdUsuarios");

        // Obtén la referencia al nombre de usuario usando el UID
        referenceIdUsuarios.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtiene el nombre de usuario asociado al UID
                    String nombreUsuario = snapshot.getValue(String.class);

                    if (nombreUsuario != null) {
                        referencetelefono.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Obtén el nombreUsuario del usuario actual

                                    String numeroActual = snapshot.child(nombreUsuario).child("telefono").getValue(String.class);

                                    if (numeroActual != null) {
                                        // Muestra el nombreUsuario en el EditText
                                        nuevoTelefono.setText(numeroActual);
                                        Log.d("UsuarioActivity", "Telefono obtenido exitosamente: " + numeroActual);
                                    } else {
                                        Log.e("UsuarioActivity", "Telefono es nulo");
                                        nuevoTelefono.setText("Error al obtener el telefono");
                                    }
                                } else {
                                    // El usuario no existe en la base de datos, maneja según tus necesidades
                                    Log.e("UsuarioActivity", "El telefono no existe en la base de datos");
                                    nuevoTelefono.setText("telefono no encontrado");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Maneja los errores si es necesario
                                Log.e("UsuarioActivity", "Error en la lectura de datos: " + error.getMessage());
                                nuevoTelefono.setText("Error en la lectura de datos");
                            }
                        });
                    } else {
                        Toast.makeText(usuarioActivity.this, "No se encontró el nombre de usuario asociado al UID", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja los errores si es necesario
                Log.e("UsuarioActivity", "Error en la lectura de datos: " + error.getMessage());
            }
        });
    }

    private void checkIfImageExists(String imageName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef2 = storage.getReference().child("images").child(imageName);
        database = FirebaseDatabase.getInstance();
        reference2 = database.getReference("IdUsuarios").child(imageName);

        storageRef2.getMetadata().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef2.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
