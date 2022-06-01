package com.netteam.unihome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class ServicioNotifs extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        final String CHANNEL_ID = "UniHome";
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.logo2)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(0,notification.build());
        }
    }

    /*@Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        actualizarToken(token);
    }

    private void actualizarToken(String token){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth autenticacion = FirebaseAuth.getInstance();
        DocumentReference tokenActual = db.collection("tokens").document(autenticacion.getUid());
        tokenActual.update("token",token);
    }*/

}