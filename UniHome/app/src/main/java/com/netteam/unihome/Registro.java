package com.netteam.unihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Registro extends AppCompatActivity {

    Button botonRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registro);

        botonRegistrar = findViewById(R.id.BotonConfirmarRegistrar);

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadMain = new Intent(v.getContext(),MainActivity.class);
                startActivity(actividadMain);
                Toast.makeText(Registro.this, "Se creo usuario correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}