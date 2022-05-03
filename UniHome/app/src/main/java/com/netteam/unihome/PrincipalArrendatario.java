package com.netteam.unihome;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.netteam.unihome.databinding.ActivityPrincipalBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PrincipalArrendatario extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityPrincipalBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng ubicacion;
    private MarkerOptions marcador;
    private Button logOut;
    private FirebaseAuth autenticacion;
    private Arrendatario datosUsuario;
    //private ImageView fotoArrendatario;
    private FirebaseStorage storage;
    private StorageReference storageRef,perfil;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marcador = new MarkerOptions();
        ubicacion = new LatLng(0,0);

        autenticacion = FirebaseAuth.getInstance();
        //fotoArrendatario = findViewById(R.id.fotoArrendatario);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        perfil = storage.getReference();

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        logOut = findViewById(R.id.CerrarSesionA);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,ubicacionObtenida);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapA);
        mapFragment.getMapAsync(this);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autenticacion.signOut();
                Intent intent = new Intent(PrincipalArrendatario.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //mostrarFoto();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        marcador.position(ubicacion).title("Ubicacion Actual");
        mMap.addMarker(marcador);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    ActivityResultLauncher<String> solicitarPermisoUbicacion = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onActivityResult(Boolean result) {
                    if(result == true){
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(PrincipalArrendatario.this,ubicacionObtenida);
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                        {
                            Toast.makeText(PrincipalArrendatario.this, "No se ha otorgado el permiso para la ubicacion.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private OnSuccessListener<Location> ubicacionObtenida = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                ubicacion = new LatLng(location.getLatitude(),location.getLongitude());
                onMapReady(mMap);
            }
        }
    };

    /*private void mostrarFoto(){
        fotoArrendatario = findViewById(R.id.fotoArrendatario);
        Picasso.get().load(autenticacion.getCurrentUser().getPhotoUrl()).into(fotoArrendatario);
    }*/
}