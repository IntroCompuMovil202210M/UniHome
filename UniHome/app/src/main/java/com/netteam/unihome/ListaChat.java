package com.netteam.unihome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ListaChat extends AppCompatActivity {

    private TextView usuarioChat;
    private ImageView fotoPerfilChat;
    private EditText mensajeChat;
    private Button enviarMensaje;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chat);

        usuarioChat = findViewById(R.id.usuarioNombreChat);
        fotoPerfilChat = findViewById(R.id.fotoPerfilChat);
        mensajeChat = findViewById(R.id.escribirMensajeChat);
        enviarMensaje = findViewById(R.id.enviarChat);
        recyclerView = findViewById(R.id.recycleChats);
    }
}