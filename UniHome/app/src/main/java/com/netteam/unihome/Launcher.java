package com.netteam.unihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Launcher extends AppCompatActivity {

    FirebaseAuth autenticacion;
    FirebaseUser usuario;
    FirebaseFirestore db;
    Arrendatario arrendatario;
    Estudiante estudiante;
    public static String CHANNEL_ID = "UniHome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        autenticacion = FirebaseAuth.getInstance();
        usuario = autenticacion.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        crearCanalNotifs();
        verificarUsuario();
    }

    @Override
    protected void onStart() {
        super.onStart();
        autenticacion = FirebaseAuth.getInstance();
        usuario = autenticacion.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    void verificarUsuario() {
        if(usuario == null) {
            startActivity(new Intent(Launcher.this, MainActivity.class));
        } else {
            buscarArrendatario(usuario.getUid());
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
                        Toast.makeText(Launcher.this, "Bienvenido/a "+arrendatario.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent iniciarArrendatario = new Intent(Launcher.this, PrincipalArrendatario.class);
                        iniciarArrendatario.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(iniciarArrendatario);
                    } else {
                        buscarEstudiante(id);
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
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
                        Toast.makeText(Launcher.this, "Bienvenido/a "+estudiante.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent iniciarEstudiante = new Intent(Launcher.this, PrincipalEstudiante.class);
                        iniciarEstudiante.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(iniciarEstudiante);
                    } else {
                        //Toast.makeText(MainActivity.this, "No existe el estudiante", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private void crearCanalNotifs(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence nombre = "notificaciones";
            String descripcion = "canal de prueba";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(CHANNEL_ID,nombre,importancia);
            canal.setDescription(descripcion);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
        }
    }

}