package com.netteam.unihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    EditText correo, contrasena;
    private FirebaseAuth autenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inicio);

        botonRegistroMain = findViewById(R.id.botonRegistrar);
        botonEntrar = findViewById(R.id.botonEntrar);
        correo = findViewById(R.id.email);
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

    private View.OnClickListener iniciarSesion = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validateForm()){
                login(correo.getText().toString(),contrasena.getText().toString());
            }else{
                Toast.makeText(Inicio.this, "Ingrese todos datos para iniciar sesión.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean validateForm() {
        boolean valid = true;
        String email = correo.getText().toString();
        if (TextUtils.isEmpty(email)) {
            valid = false;
        }
        String password = contrasena.getText().toString();
        if (TextUtils.isEmpty(password)) {
            valid = false;
        }
        return valid;
    }

    private void login(String email, String password) {
        if (validateForm()) {
            autenticacion.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Inicio.this, "Login satisfactorio.", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = autenticacion.getCurrentUser();
                                updateUI(user);
                            } else {
                                Toast.makeText(Inicio.this, "Login fallido.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = autenticacion.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(Inicio.this, Principal.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            correo.setText("");
            contrasena.setText("");
        }
    }

}