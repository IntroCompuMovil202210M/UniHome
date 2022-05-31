package com.netteam.unihome;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Publicar extends AppCompatActivity {

    private Button BotonTomarFotoP,BotonSelectImgP,agregarPublicacion,botonCancelar;
    private EditText NombreResidencia,DireccionResidencia,DescripcionResidencia;
    private ImageView FotoPublicacion;
    FirebaseAuth autenticacion;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef,ruta;
    Uri uriFoto,uridescarga;
    boolean fotoSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);
        BotonSelectImgP = findViewById(R.id.BotonSelectImgP);
        BotonTomarFotoP = findViewById(R.id.BotonTomarFotoP);
        agregarPublicacion = findViewById(R.id.agregarPublicacion);
        botonCancelar = findViewById(R.id.botonCancelar);
        NombreResidencia = findViewById(R.id.nombreResidencia);
        DireccionResidencia = findViewById(R.id.direccionResidencia);
        DescripcionResidencia = findViewById(R.id.descripcionResidencia);
        FotoPublicacion = findViewById(R.id.fotoPublicacion);
        fotoSeleccionada=false;
        autenticacion = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        BotonSelectImgP.setOnClickListener(establecerfotoPerfil);
        BotonTomarFotoP.setOnClickListener(tomarfotoPerfil);
        agregarPublicacion.setOnClickListener(publicar);

        File archivo = new File(getFilesDir(),"fotoCamara");
        uriFoto = FileProvider.getUriForFile(this,getApplicationContext().getPackageName()+".fileprovider",archivo);
        botonCancelar.setOnClickListener(cancelar);
    }

    private boolean validar(String nombre, String descripcion, String direccion){
        if(TextUtils.isEmpty(nombre) || TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(direccion)){
            Toast.makeText(this, "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!fotoSeleccionada){
            Toast.makeText(this, "Debe seleccionar una foto de la residencia.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void agregarResidencia(){
        Map<String, Object> residencia = new HashMap<>();
        residencia.put("nombre", NombreResidencia.getText().toString());
        residencia.put("descripcion", DescripcionResidencia.getText().toString());
        residencia.put("direccion",DireccionResidencia.getText().toString());
        residencia.put("arrendatario",autenticacion.getCurrentUser().getUid());
        residencia.put("foto",uridescarga);

        db.collection("residencias").document(NombreResidencia.getText().toString())
                .set(residencia)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Publicar.this, "Se publicó la residencia correctamente.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Publicar.this,PrincipalArrendatario.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Publicar.this, "No se pudo crear la publicación.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    ActivityResultLauncher<String> obtenerImagen = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    //Carga una imagen en la vista...
                    FotoPublicacion.setImageURI(result);
                    uriFoto = result;
                    fotoSeleccionada=true;
                }
            }
    );

    ActivityResultLauncher<Uri> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    FotoPublicacion.setImageURI(uriFoto);
                    fotoSeleccionada=true;
                }
            }
    );

    ActivityResultLauncher<String> solicitarpermisoCamara = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onActivityResult(Boolean result) {
                    if(result){
                        tomarFoto.launch(uriFoto);
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                        {
                            Toast.makeText(Publicar.this, "Debe otorgar el acceso a la cámara.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    ActivityResultLauncher<String> solicitarpermisoAlmacenamiento = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onActivityResult(Boolean result) {
                    if(result){
                        obtenerImagen.launch("image/*");
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                        {
                            Toast.makeText(Publicar.this, "Debe otorgar el acceso al almacenamiento.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private View.OnClickListener publicar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validar(NombreResidencia.getText().toString(),DescripcionResidencia.getText().toString(),DireccionResidencia.getText().toString())){
                ruta = storageRef.child("residencias/"+NombreResidencia.getText().toString());
                UploadTask uploadTask = ruta.putFile(uriFoto);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return ruta.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    uridescarga = task.getResult();
                                    agregarResidencia();
                                }
                            }
                        });
                    }
                });
            }
            }
    };

    View.OnClickListener establecerfotoPerfil = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            solicitarpermisoAlmacenamiento.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    };

    View.OnClickListener tomarfotoPerfil = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            solicitarpermisoCamara.launch(Manifest.permission.CAMERA);
        }
    };

    private View.OnClickListener cancelar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent cancelar = new Intent(Publicar.this,PrincipalArrendatario.class);
            startActivity(cancelar);
        }
    };
}