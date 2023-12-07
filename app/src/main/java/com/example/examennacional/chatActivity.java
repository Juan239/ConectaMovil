package com.example.examennacional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examennacional.Controladores.MqttHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;

public class chatActivity extends AppCompatActivity {
    private TextView nombreReceptor;
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";

    private MqttHandler mqttHandler;
    private ImageView btnEnviarMensaje;
    private EditText mensajeET;
    private DatabaseReference databaseReference, referenceid_usuario;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private static String CLIENT_ID;
    private String currentUser, nombreUsuarioCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        nombreReceptor = findViewById(R.id.nombreReceptor);
        mAuth = FirebaseAuth.getInstance();
        CLIENT_ID = mAuth.getUid();
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        mensajeET = findViewById(R.id.ETmensaje);

        currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Mensajes");
        referenceid_usuario = firebaseDatabase.getReference().child("IdUsuarios").child(currentUser);

        referenceid_usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtén el valor del nodo
                    String usuarioActual = dataSnapshot.getValue(String.class);

                    // Realiza acciones con valorUsuario
                    Intent intent = getIntent();
                    String nombreUsuarioReceptor =intent.getStringExtra("Nombre_receptor");
                    Toast.makeText(chatActivity.this, "Nombre de usuario: " + usuarioActual+"chat"+nombreUsuarioReceptor, Toast.LENGTH_SHORT).show();


                    nombreReceptor.setText(nombreUsuarioReceptor);

                    mqttHandler = new MqttHandler();
                    mqttHandler.connect(BROKER_URL, CLIENT_ID);

                    String topic = usuarioActual+"chat"+nombreUsuarioReceptor;
                    String topicContrario = nombreUsuarioReceptor+"chat"+usuarioActual;

                    // Suscribirse al tema antes de publicar (para recibir tus propios mensajes)
                    subscribeTopic(topic);
                    subscribeTopic(topicContrario);

                    btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mensaje = mensajeET.getText().toString();

                            // Verificar si el mensaje no está vacío
                            if (!mensaje.isEmpty()) {


                                // Identificar el origen del mensaje (en este caso, Android)
                                String origin = usuarioActual;

                                // Construir el mensaje completo con información adicional
                                String fullMessage = origin + ":" + mensaje;



                                // Publicar el mensaje
                                publicMessage(topic, fullMessage);



                                // Limpiar el EditText después de enviar el mensaje
                                mensajeET.setText("");
                            } else {
                                Toast.makeText(chatActivity.this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    //Seguir aca
                    // Suscribirse al tema de chat

                    //Si no llega a funcionar es este el que hay que habilitar
                    //subscribeTopic(usuarioActual+"chat"+nombreUsuarioReceptor);

                    // Manejar mensajes entrantes
                    mqttHandler.setCallback(new MqttCallback() {
                        @Override
                        public void messageArrived(String topic, MqttMessage message) {
                            // Este método se llama cuando se recibe un mensaje en el tema suscrito
                            String incomingMessage = new String(message.getPayload());

                            // Dividir el mensaje en dos partes: origen y contenido
                            String[] parts = incomingMessage.split(":", 2);

                            // Verificar si el mensaje tiene el formato esperado (origen:contenido)
                            if (parts.length == 2) {
                                String sender = parts[0].trim();
                                String messageContent = parts[1].trim();

                                // Actualizar la interfaz de usuario con el remitente y el contenido del mensaje
                                updateChatView(sender, messageContent);
                            } else {
                                // Si no se puede dividir el mensaje correctamente, asumir que proviene de MyMQTT
                                String sender = nombreUsuarioReceptor;
                                String messageContent = incomingMessage.trim();

                                // Actualizar la interfaz de usuario con el remitente y el contenido del mensaje
                                updateChatView(sender, messageContent);
                            }
                        }


                        @Override
                        public void connectionLost(Throwable cause) {
                            // Manejar la pérdida de conexión (si es necesario)
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                            // Manejar la entrega completa (si es necesario)
                        }
                    });




                } else {
                    // El nodo no existe, maneja este caso según tus necesidades
                    Toast.makeText(chatActivity.this, "Nodo no encontrado", Toast.LENGTH_SHORT).show();
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
                Log.e("ErrorFirebase", "Error al leer datos", databaseError.toException());
            }
        });



        
    }

    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();
    }

    private void publicMessage(String topic, String message) {
        // Publicar el mensaje
        mqttHandler.publish(topic, message);
    }

    private void subscribeTopic(String topic) {
        // Suscribirse al tema
        mqttHandler.subscribe(topic);
    }

    private void updateChatView(final String sender, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Obtén el layout donde se mostrarán los mensajes
                LinearLayout chatMessageLayout = findViewById(R.id.chatMessageLayout);

                // Crea un nuevo TextView para el mensaje
                TextView messageTextView = new TextView(chatActivity.this);

                // Configura el texto con el remitente y el mensaje
                String formattedMessage = sender + ": " + message;
                messageTextView.setText(formattedMessage);

                // Crea un nuevo parámetro de diseño para el TextView
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                // Ajusta la gravedad del texto según el remitente
                if (sender.equals(nombreReceptor.getText())) {
                    // Si el mensaje proviene de MyMQTT, alinea a la derecha
                    layoutParams.gravity = Gravity.END;
                } else {
                    // Si el mensaje proviene de Android, alinea a la izquierda
                    layoutParams.gravity = Gravity.START;
                }

                // Establece los parámetros de diseño en el TextView
                messageTextView.setLayoutParams(layoutParams);

                // Añade el TextView al layout, al final para que aparezca al final de la lista
                chatMessageLayout.addView(messageTextView);

                // Desplázate hacia abajo para mostrar el mensaje más reciente
                ScrollView scrollView = findViewById(R.id.scrollView);
                scrollView.fullScroll(View.FOCUS_DOWN);
                saveMessageToFirebase(sender, message);
            }
        });
    }

    private void saveMessageToFirebase(String sender, String message) {
        // Crea un nuevo nodo con un identificador único para cada mensaje
        String messageId = databaseReference.push().getKey();

        // Guarda el mensaje en el nodo correspondiente
        databaseReference.child(messageId).child("sender").setValue(sender);
        databaseReference.child(messageId).child("message").setValue(message);
    }
}