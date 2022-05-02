package com.netteam.unihome;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MetodosFB {
    static FirebaseFirestore db;
    static Arrendatario arrendatario;
    static Estudiante estudiante;

    public static void IniciarBD(){
        db = FirebaseFirestore.getInstance();
    }

    public static Arrendatario buscarArrendatario(String idusuario){
        DocumentReference docRef = db.collection("arrendatarios").document(idusuario);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        arrendatario = document.toObject(Arrendatario.class);
                    } else {
                        arrendatario = null;
                    }
                } else {
                    Log.i("BD","Excepción generada "+ task.getException());
                }
            }
        });
        return arrendatario;
    }

    public static Estudiante buscarEstudiante(String idusuario){
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("estudiantes").document(idusuario);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        estudiante = document.toObject(Estudiante.class);
                    } else {
                        estudiante = null;
                    }
                } else {
                    Log.i("BD","Excepción generada "+ task.getException());
                }
            }
        });
        return estudiante;
    }
}
