package com.netteam.unihome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

public class ServicioNotifs extends FirebaseMessagingService {
    private FirebaseFirestore db;
    private FirebaseAuth autenticacion;
    public ServicioNotifs() {

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        actualizarToken(token);
    }

    private void actualizarToken(String token){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth autenticacion = FirebaseAuth.getInstance();
        DocumentReference tokenActual = db.collection("tokens").document(autenticacion.getUid());
        tokenActual.update("token",token);
    }

}