package com.netteam.unihome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button botonRegistrar,botonEntrar;
    EditText correo, contrasena;
    private FirebaseAuth autenticacion;
    private FirebaseFirestore db;
    private Arrendatario arrendatario;
    private Estudiante estudiante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        arrendatario = null;
        botonRegistrar = findViewById(R.id.botonRegistrarA);
        botonEntrar = findViewById(R.id.botonEntrar);
        correo = findViewById(R.id.email);
        contrasena = findViewById(R.id.contrasena);
        autenticacion = FirebaseAuth.getInstance();

        botonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loggearUsuario();
            }
        });

        botonRegistrar.setOnClickListener(registrarse);
    }

    private void loggearUsuario(){
        String correoE = correo.getText().toString();
        String contrasenaE = contrasena.getText().toString();

        if(validarCampos(correoE,contrasenaE)){
            autenticacion.signInWithEmailAndPassword(correoE,contrasenaE).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.i("INFO","LOGGEADO CORRECTO");
                        FirebaseUser usuarioActual = autenticacion.getCurrentUser();
                        buscarArrendatario(autenticacion.getUid());
                        updateUI(usuarioActual);
                    }else{
                        String error = task.getException().getMessage();
                        Log.i("INFO",error);
                        Toast.makeText(MainActivity.this,error,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            correo.setText("");
            contrasena.setText("");
        }
    }

    private boolean validarCampos(String email,String contra){
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(contra)){
            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(contra)) {
                Toast.makeText(this,"No se ha Ingresado el Email y la Contraseña",Toast.LENGTH_LONG).show();
            }else{
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(this,"No se ha Ingresado el Email",Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(contra)){
                    Toast.makeText(this,"No se ha Ingresado la Contraseña",Toast.LENGTH_LONG).show();
                }
            }
            return false;
        }else{
            if(validarCorreo(email) && validarContra(contra)){
                return true;
            }else{
                if(!validarCorreo(email) && !validarContra(contra))
                    Toast.makeText(this,"Correo y Contraseña no Validos",Toast.LENGTH_LONG).show();
                else{
                    if(!validarCorreo(email))
                        Toast.makeText(this,"Correo Ingresado no Valido",Toast.LENGTH_LONG).show();
                    if(!validarContra(contra))
                        Toast.makeText(this,"Contraseña Ingresada no Valida",Toast.LENGTH_LONG).show();
                }
                return false;
            }
        }
    }

    private boolean validarCorreo(String email){
        if(!email.contains("@") || !email.contains(".") || email.length() < 5)
            return false;
        return true;
    }

    private boolean validarContra(String contra){
        if(contra.length() < 4)
            return false;
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = autenticacion.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser usuarioActual){
        if(usuarioActual != null){
            Intent actividadInicio = new Intent(this, PrincipalArrendatario.class);
            startActivity(actividadInicio);
        }else{
            correo.setText("");
            contrasena.setText("");
        }
    }

    private void buscarArrendatario(String id){
        DocumentReference docRef = db.collection("arrendatarios").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        arrendatario = document.toObject(Arrendatario.class);
                        Toast.makeText(MainActivity.this, "Bienvenido/a "+arrendatario.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent iniciarArrendatario = new Intent(MainActivity.this, PrincipalArrendatario.class);
                        startActivity(iniciarArrendatario);
                    } else {
                        //Toast.makeText(MainActivity.this, "No existe el arrendatario", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private void buscarEstudiante(String id){
        DocumentReference docRef = db.collection("estudiantes").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        estudiante = document.toObject(Estudiante.class);
                        Toast.makeText(MainActivity.this, "Bienvenido/a "+estudiante.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent iniciarArrendatario = new Intent(MainActivity.this, PrincipalArrendatario.class);
                        startActivity(iniciarArrendatario);
                    } else {
                        //Toast.makeText(MainActivity.this, "No existe el arrendatario", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("BD", "Excepción: "+ task.getException());
                }
            }
        });
    }

    private View.OnClickListener registrarse = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent actividadRegistro = new Intent(MainActivity.this, RegistroArrendatario.class);
            startActivity(actividadRegistro);
        }
    };
}