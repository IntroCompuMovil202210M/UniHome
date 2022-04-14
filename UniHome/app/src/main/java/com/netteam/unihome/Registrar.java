package com.netteam.unihome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class Registrar extends AppCompatActivity {

    Button botonRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registrar);

        botonRegistrar = findViewById(R.id.BotonConfirmarRegistrar);

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadMain = new Intent(v.getContext(),Inicio.class);
                startActivity(actividadMain);
                Toast.makeText(Registrar.this, "Se creo Usuario Correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}