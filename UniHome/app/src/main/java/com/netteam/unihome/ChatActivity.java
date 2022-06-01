package com.netteam.unihome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netteam.unihome.adapters.MensajeAdapter;
import com.netteam.unihome.models.Mensaje;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

        autenticacion = FirebaseAuth.getInstance();
        usuario = autenticacion.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("chat");
        db = FirebaseFirestore.getInstance();

        buscarEstudiante(usuario.getUid());

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

        databaseReference.addChildEventListener(new ChildEventListener() {
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
        });
    }

    private void buscarEstudiante(String id){
        DocumentReference docRef = db.collection("estudiantes").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        estudiante = document.toObject(Estudiante.class);
                        nombreUsuarioChat.setText(estudiante.getNombre()+" "+estudiante.getApellido());
                    } else {
                        estudiante = null;
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private void setScrollBar(){
        rvMensajes.scrollToPosition(adaptador.getItemCount()-1);
    }

    private void anadirMensaje(){
        if(inputMensaje.getText().toString() != null){
            horaActual = LocalTime.now();
            DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm");
            databaseReference.push().setValue(new Mensaje(inputMensaje.getText().toString(),nombreUsuarioChat.getText().toString(),horaActual.format(f).toString()));
            Log.i("CHAT","NOMBREUSUARIO: " + nombreUsuarioChat.getText().toString()+" MENSAJE: " + inputMensaje.getText().toString());
            Log.i("CHAT", "LISTA "+ adaptador.getItemCount());
            inputMensaje.setText("");
        }
    }
}