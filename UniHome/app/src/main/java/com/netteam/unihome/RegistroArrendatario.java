package com.netteam.unihome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroArrendatario extends AppCompatActivity {

    Button botonRegistrar;
    EditText nombre,apellido,emailRegistro,contraseña,confirmarc;
    FirebaseAuth autenticacion;
    FirebaseFirestore db;
    Spinner tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registro);

        autenticacion = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        nombre = findViewById(R.id.nombre);
        apellido = findViewById(R.id.apellido);
        emailRegistro = findViewById(R.id.emailRegistro);
        contraseña = findViewById(R.id.contrasenaRegistro);
        confirmarc = findViewById(R.id.cofirmacionContrasena);
        tipo = findViewById(R.id.tipoCuenta);
        botonRegistrar = findViewById(R.id.registrarse);

        botonRegistrar.setOnClickListener(registro);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuario = autenticacion.getCurrentUser();
        if(usuario != null){
            //reload();
        }
    }

    private View.OnClickListener registro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validar(nombre.getText().toString(),apellido.getText().toString(),
                    emailRegistro.getText().toString(),contraseña.getText().toString(),
                    confirmarc.getText().toString())){
                registrarUsuario();
            }
        }
    };

    private boolean validar(String nombre, String apellido, String email, String contra, String conf){
        if(TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) || TextUtils.isEmpty(email) || TextUtils.isEmpty(contra) || TextUtils.isEmpty(conf)){
            Toast.makeText(this, "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!validarCorreo(email)){
            Toast.makeText(this, "Ingrese un correo válido.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!validarContra(contra)){
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!contra.equals(conf)){
            Toast.makeText(this, "Las contraseñas no concuerdan.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean validarCorreo(String email){
        if(!email.contains("@") || !email.contains(".") || email.length() < 5)
            return false;
        return true;
    }

    private boolean validarContra(String contra){
        if(contra.length() < 6)
            return false;
        return true;
    }

    private void registrarUsuario(){
        autenticacion.createUserWithEmailAndPassword(emailRegistro.getText().toString(),contraseña.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("BD","Se creo el usuario.");
                    FirebaseUser usuario = autenticacion.getCurrentUser();
                    agregarArrendatario(usuario.getUid());
                }else{
                    Log.i("BD","El usuario no se creo.");
                }
            }
        });
    }

    private void agregarArrendatario(String id){
        Log.i("BD","ID:"+id);
        Map<String, Object> arrendatario = new HashMap<>();
        arrendatario.put("nombre", nombre.getText().toString());
        arrendatario.put("apellido", apellido.getText().toString());

        db.collection("arrendatarios").document(id)
                .set(arrendatario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegistroArrendatario.this, "Se creó el usuario correctamente.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistroArrendatario.this,MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroArrendatario.this, "No se pudo crear el usuario.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}