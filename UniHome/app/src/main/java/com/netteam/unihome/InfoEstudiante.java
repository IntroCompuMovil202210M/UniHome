package com.netteam.unihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class InfoEstudiante extends AppCompatActivity {

    private Button sesionE;
    private TextView nombreApellidoE, universidadE, programaE;
    private ImageView fotoperfilE;
    private FirebaseUser usuario;
    private FirebaseAuth autenticacion;
    private FirebaseFirestore db;
    private Estudiante estudiante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_estudiante);
        sesionE = findViewById(R.id.sesionE);
        nombreApellidoE = findViewById(R.id.nombreApellidoE);
        universidadE = findViewById(R.id.universidadE);
        programaE = findViewById(R.id.programaE);
        fotoperfilE = findViewById(R.id.fotoperfilE);
        autenticacion = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sesionE.setOnClickListener(cerrarsesion);
        cargarDatos();
    }

    private void cargarDatos(){
        usuario = autenticacion.getCurrentUser();
        buscarEstudiante(usuario.getUid());
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
                        nombreApellidoE.setText(estudiante.getNombre()+" "+estudiante.getApellido());
                        universidadE.setText(estudiante.getUniversidad());
                        programaE.setText(estudiante.getPrograma());
                        mostrarFoto();
                    } else {
                        estudiante = null;
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private void mostrarFoto(){
        fotoperfilE = findViewById(R.id.fotoperfilE);
        Picasso.get().load(usuario.getPhotoUrl()).into(fotoperfilE);
    }

    private View.OnClickListener cerrarsesion = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            autenticacion.signOut();
            Intent volveraInicio = new Intent(InfoEstudiante.this,MainActivity.class);
            volveraInicio.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(volveraInicio);
        }
    };
}