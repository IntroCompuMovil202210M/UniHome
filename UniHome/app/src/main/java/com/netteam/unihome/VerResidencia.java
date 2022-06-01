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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class VerResidencia extends AppCompatActivity {

    private TextView nombreResidenciaVer,DireccionResidencia,descripcionResidenciaVer;
    private ImageView fotoResidencia;
    private Button iniciarChatVer;
    private FirebaseFirestore db;
    private FirebaseAuth autenticacion;
    private Residencia residencia,aux;
    private String arrendatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_residencia);

        nombreResidenciaVer = findViewById(R.id.nombreResidenciaVer);
        DireccionResidencia = findViewById(R.id.DireccionResidencia);
        descripcionResidenciaVer = findViewById(R.id.descripcionResidenciaVer);
        fotoResidencia = findViewById(R.id.fotoResidencia);
        iniciarChatVer = findViewById(R.id.iniciarChatVer);

        db = FirebaseFirestore.getInstance();
        autenticacion = FirebaseAuth.getInstance();
        aux = new Residencia();

        Bundle info = getIntent().getBundleExtra("bundle");

        arrendatario = info.getString("arrendatario");

        nombreResidenciaVer.setText(info.getString("nombre"));
        DireccionResidencia.setText(info.getString("direccion"));
        descripcionResidenciaVer.setText(info.getString("descripcion"));
        Picasso.get().load(info.getString("foto")).into(fotoResidencia);


        iniciarChatVer.setOnClickListener(irChat);
    }

    private void busquedaResidencia (String id){
        DocumentReference docRef = db.collection("residencias").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        residencia = document.toObject(Residencia.class);
                    }
                }
                else{
                    Log.i("loco", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private View.OnClickListener irChat = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent irChat = new Intent(VerResidencia.this,ChatActivity.class);
            irChat.putExtra("idChat",autenticacion.getUid()+arrendatario);
            Map<String, Object> chat = new HashMap<>();
            chat.put("estudiante", autenticacion.getUid());
            chat.put("arrendatario", arrendatario);
            db.collection("chats").document(autenticacion.getUid()+arrendatario).set(chat);
            startActivity(irChat);
        }
    };
}