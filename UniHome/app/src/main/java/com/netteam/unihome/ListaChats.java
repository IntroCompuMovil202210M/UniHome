package com.netteam.unihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netteam.unihome.models.Chat;

import java.util.ArrayList;

public class ListaChats extends AppCompatActivity {

    private ListView listaChats;
    private ArrayList<String> nombres;
    private ArrayAdapter<String> adapter;
    private boolean esEstudiante;
    private Estudiante estudiante;
    private FirebaseFirestore db;
    private FirebaseAuth autenticacion;
    private Chat chat;
    private Arrendatario arrendatario;
    private ArrayList<String> idChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chats);

        idChats = new ArrayList<String>();
        arrendatario = new Arrendatario();
        chat = new Chat();
        esEstudiante = false;
        estudiante = new Estudiante();
        autenticacion = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        nombres = new ArrayList<String>();
        listaChats = findViewById(R.id.listaChats);
        adapter = new ArrayAdapter<String>(this, android.R.layout.activity_list_item,nombres);
        listaChats.setAdapter(adapter);
        Log.i("info","se inicio la lectura dle chat");
        buscarEstudiante(autenticacion.getUid());
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
                        esEstudiante = true;
                        leerChats();
                    } else {
                        esEstudiante = false;
                        leerChats();
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private void leerChats(){
        if(esEstudiante){
            Query consulta = db.collection("chats").whereEqualTo("estudiante",autenticacion.getUid());
            for (QueryDocumentSnapshot doc:consulta.get().getResult()) {
                chat = doc.toObject(Chat.class);
                buscarArrendatario(chat.getArrendatario());
                idChats.add(doc.getId());
            }
        }else{
            Query consulta = db.collection("chats").whereEqualTo("arrendatario",autenticacion.getUid());
            for (QueryDocumentSnapshot doc:consulta.get().getResult()) {
                chat = doc.toObject(Chat.class);
                buscarEstudianteDos(chat.getArrendatario());
                nombres.add(estudiante.getNombre());
                idChats.add(doc.getId());
            }
        }
    }

    private void buscarArrendatario(String id){
        DocumentReference docRef = db.collection("arrendatarios").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        arrendatario = document.toObject(Arrendatario.class);
                        nombres.add(arrendatario.getNombre());
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private void buscarEstudianteDos(String id){
        DocumentReference docRef = db.collection("estudiantes").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        estudiante = document.toObject(Estudiante.class);
                        nombres.add(estudiante.getNombre());
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }
}