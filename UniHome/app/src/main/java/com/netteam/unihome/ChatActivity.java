package com.netteam.unihome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.netteam.unihome.adapters.MensajeAdapter;
import com.netteam.unihome.models.Mensaje;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private TextView nombreUsuarioChat;
    private EditText inputMensaje;
    private Button botonEnviar;
    private RecyclerView rvMensajes;
    private MensajeAdapter adaptador;
    private LocalTime horaActual;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser usuario;
    private FirebaseAuth autenticacion;
    private Estudiante estudiante;
    private FirebaseFirestore db;
    private CollectionReference docRef;
    private int contadorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nombreUsuarioChat = findViewById(R.id.nombreUsuarioChat);
        inputMensaje = findViewById(R.id.inputMensaje);
        rvMensajes = findViewById(R.id.chatRecycler);
        botonEnviar = findViewById(R.id.botonEnviarMensaje);

        adaptador = new MensajeAdapter(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adaptador);

        contadorMsg = 1000;

        autenticacion = FirebaseAuth.getInstance();
        usuario = autenticacion.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");
        db = FirebaseFirestore.getInstance();
        String idChat = getIntent().getStringExtra("idChat");
        docRef = db.collection("chats").document(idChat).collection("mensajes");

        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anadirMensaje();
            }
        });

        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });

        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.i("TAG", "Listen failed. " + e);
                            return;
                        }
                        adaptador = new MensajeAdapter(ChatActivity.this);
                        contadorMsg = 1000;

                        for (QueryDocumentSnapshot doc : value) {
                            adaptador.addMensaje(doc.toObject(Mensaje.class));
                            contadorMsg++;
                            Log.i("msg","Mensaje Leido");
                        }
                        rvMensajes.setAdapter(adaptador);
                    }
        });

        /*databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensaje m = snapshot.getValue(Mensaje.class);
                adaptador.addMensaje(m);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void setScrollBar(){
        rvMensajes.scrollToPosition(adaptador.getItemCount());
    }

    private void anadirMensaje(){
        if(inputMensaje.getText().toString() != null){
            horaActual = LocalTime.now();
            DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm");
            Map<String, Object> mensaje = new HashMap<>();
            mensaje.put("msg", inputMensaje.getText().toString());
            mensaje.put("nombre", usuario.getDisplayName());
            mensaje.put("hora", horaActual.format(f).toString());
            mensaje.put("pos",contadorMsg+1);
            docRef.document(Integer.toString(contadorMsg+1)).set(mensaje);
            inputMensaje.setText("");
        }
    }
}