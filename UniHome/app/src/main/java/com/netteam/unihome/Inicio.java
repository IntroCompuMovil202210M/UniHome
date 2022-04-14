package com.netteam.unihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Inicio extends AppCompatActivity {

    Button botonRegistroMain,botonEntrar;
    EditText email, contrasena;
    private FirebaseAuth autenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inicio);

        botonRegistroMain = findViewById(R.id.botonRegistrar);
        botonEntrar = findViewById(R.id.botonEntrar);
        email = findViewById(R.id.email);
        contrasena = findViewById(R.id.contrasena);
        autenticacion = FirebaseAuth.getInstance();

        botonRegistroMain.setOnClickListener(registrarse);

        botonEntrar.setOnClickListener(iniciarSesion);

    }

    private View.OnClickListener registrarse = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent actividadRegistro = new Intent(view.getContext(),Registrar.class);
            startActivity(actividadRegistro);
        }
    };

    private boolean validarDatos(){
        if(!email.getText().toString().isEmpty() && !contrasena.getText().toString().isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    private View.OnClickListener iniciarSesion = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validarDatos()){
                autenticacion.signInWithEmailAndPassword(email.getText().toString(), contrasena.getText().toString()).addOnCompleteListener(Inicio.this,login);
            }else{
                Toast.makeText(Inicio.this, "Ingrese todos datos para iniciar sesión.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnCompleteListener<AuthResult> login = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Toast.makeText(Inicio.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                FirebaseUser user = autenticacion.getCurrentUser();
                updateUI(user);
            }else{
                Toast.makeText(Inicio.this, "Inicio de sesión fallido.", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = autenticacion.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(getBaseContext(), Inicio.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            email.setText("");
            contrasena.setText("");
        }
    }

}