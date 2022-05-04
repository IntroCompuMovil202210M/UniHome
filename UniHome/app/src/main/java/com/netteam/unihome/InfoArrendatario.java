package com.netteam.unihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class InfoArrendatario extends AppCompatActivity {

    private Button sesionA,eliminarA,actualizarA;
    private TextView nombreApellidoA;
    private ImageView fotoperfilA;
    private FirebaseUser usuario;
    private FirebaseAuth autenticacion;
    private FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef,foto;
    private Arrendatario arrendatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_arrendatario);
        sesionA = findViewById(R.id.sesionA);
        nombreApellidoA = findViewById(R.id.nombreApellidoA);
        fotoperfilA = findViewById(R.id.fotoperfilA);
        eliminarA = findViewById(R.id.eliminarA);
        autenticacion = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        sesionA.setOnClickListener(cerrarsesion);
        eliminarA.setOnClickListener(eliminarPerfil);
        cargarDatos();
    }

    private void cargarDatos(){
        usuario = autenticacion.getCurrentUser();
        buscarArrendatario(usuario.getUid());
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
                        nombreApellidoA.setText(arrendatario.getNombre()+" "+arrendatario.getApellido());
                        mostrarFoto();
                    } else {
                        arrendatario = null;
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private void mostrarFoto(){
        fotoperfilA = findViewById(R.id.fotoperfilA);
        Picasso.get().load(usuario.getPhotoUrl()).into(fotoperfilA);
    }

    private View.OnClickListener cerrarsesion = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            autenticacion.signOut();
            Intent volveraInicio = new Intent(InfoArrendatario.this,MainActivity.class);
            volveraInicio.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(volveraInicio);
        }
    };

    private View.OnClickListener eliminarPerfil = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseUser usuario = autenticacion.getCurrentUser();
            foto = storageRef.child("fotos/"+usuario.getEmail());
            foto.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    db.collection("arrendatarios").document(usuario.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            usuario.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(InfoArrendatario.this, "Cuenta eliminada correctamente.", Toast.LENGTH_SHORT).show();
                                    Intent volveraInicio = new Intent(InfoArrendatario.this,MainActivity.class);
                                    volveraInicio.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(volveraInicio);
                                }
                            });
                        }
                    });
                }
            });
        }
    };
}