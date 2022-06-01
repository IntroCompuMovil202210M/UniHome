package com.netteam.unihome;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import com.google.firebase.firestore.QuerySnapshot;
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
        listaChats = (ListView) findViewById(R.id.listaChats);
        buscarEstudiante(autenticacion.getUid());
        listaChats.setAdapter(adapter);
    }

    private void buscarEstudiante(String id){
        DocumentReference docRef = db.collection("estudiantes").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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
            db.collection("chats").whereEqualTo("estudiante",autenticacion.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                chat = document.toObject(Chat.class);
                                buscarArrendatario(chat.getArrendatario());
                                idChats.add(document.getId());
                            }
                        }
                    }
                });
        }else{
            db.collection("chats").whereEqualTo("arrendatario",autenticacion.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                chat = document.toObject(Chat.class);
                                buscarEstudianteDos(chat.getEstudiante());
                                idChats.add(document.getId());
                            }
                        }
                    }
                });
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nombres);
        listaChats.setAdapter(adapter);
    }

    private void buscarArrendatario(String id){
        DocumentReference docRef = db.collection("arrendatarios").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i("bandera","Arrendatario encontrado!");
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
                        Log.i("bandera","Estudiante encontrado!");
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