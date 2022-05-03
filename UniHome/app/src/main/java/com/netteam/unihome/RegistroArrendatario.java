package com.netteam.unihome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class RegistroArrendatario extends AppCompatActivity {

    Button botonRegistrar;
    EditText nombre,apellido,emailRegistro,contraseña,confirmarc;
    ImageView fotoA;
    FirebaseAuth autenticacion;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef,perfil;
    Uri uriFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registro);

        autenticacion = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        fotoA = findViewById(R.id.fotoA);
        nombre = findViewById(R.id.nombreA);
        apellido = findViewById(R.id.apellidoA);
        emailRegistro = findViewById(R.id.emailRegistroA);
        contraseña = findViewById(R.id.contrasenaRegistroA);
        confirmarc = findViewById(R.id.cofirmacionContrasenaA);
        botonRegistrar = findViewById(R.id.registrarseA);
        botonRegistrar.setOnClickListener(registro);
        fotoA.setOnClickListener(establecerfotoPerfil);
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
                    usuario.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nombre.getText().toString()).build());
                    subirFoto();
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

    ActivityResultLauncher<String> obtenerImagen = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    //Carga una imagen en la vista...
                    fotoA.setImageURI(result);
                    uriFoto = result;
                }
            }
    );

    public void subirFoto(){
        perfil = storageRef.child("fotos/"+autenticacion.getUid());
        UploadTask uploadTask = perfil.putFile(uriFoto);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return perfil.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            autenticacion.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build());
                        }
                    }
                });
            }
        });
    }

    View.OnClickListener establecerfotoPerfil = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            obtenerImagen.launch("image/*");
        }
    };

    ActivityResultLauncher<Uri> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    //imagen.setImageURI(uricamara);
                }
            }
    );
}