package com.example.unihome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button botonRegistroMain,botonEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        botonRegistroMain = findViewById(R.id.botonRegistrar);
        botonEntrar = findViewById(R.id.botonEntrar);

        botonRegistroMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadRegistro = new Intent(v.getContext(),Registrar.class);
                startActivity(actividadRegistro);
            }
        });

        botonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadPrincipal = new Intent(v.getContext(),Principal.class);
                startActivity(actividadPrincipal);
            }
        });

    }
}