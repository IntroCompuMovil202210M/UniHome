package com.netteam.unihome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.netteam.unihome.adapters.MensajeAdapter;
import com.netteam.unihome.models.Mensaje;

import java.util.Observer;

public class ChatActivity extends AppCompatActivity {

    private TextView nombreUsuarioChat;
    private EditText inputMensaje;
    private Button botonEnviar;
    private RecyclerView rvMensajes;

    private MensajeAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nombreUsuarioChat = findViewById(R.id.nombreUsuarioChat);
        inputMensaje = findViewById(R.id.inputMensaje);
        rvMensajes = findViewById(R.id.chatRecycler);
        botonEnviar = findViewById(R.id.botonEnviarMensaje);

        adaptador = new MensajeAdapter(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adaptador);

        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adaptador.addMensaje(new Mensaje(inputMensaje.getText().toString(),nombreUsuarioChat.getText().toString(),"00:00"));
                Log.i("CHAT","NOMBREUSUARIO: " + nombreUsuarioChat.getText().toString()+" MENSAJE: " + inputMensaje.getText().toString());
                Log.i("CHAT", "LISTA "+ adaptador.getItemCount());
            }
        });

        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });
    }

    private void setScrollBar(){
        rvMensajes.scrollToPosition(adaptador.getItemCount()-1);
    }

    private void anadirMensaje(){
        adaptador.addMensaje(new Mensaje(inputMensaje.getText().toString(),nombreUsuarioChat.getText().toString(),"00:00"));
    }
}