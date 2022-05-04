package com.netteam.unihome;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.netteam.unihome.databinding.ActivityPrincipalBinding;
import com.netteam.unihome.databinding.ActivityPrincipalEstudianteBinding;

public class PrincipalEstudiante extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityPrincipalEstudianteBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng ubicacion;
    private MarkerOptions marcador;
    private ImageButton cuentaE, chatsE, publicarE;
    private FirebaseAuth autenticacion;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrincipalEstudianteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        marcador = new MarkerOptions();
        ubicacion = new LatLng(0,0);

        cuentaE = findViewById(R.id.cuentaE);
        chatsE = findViewById(R.id.chatsE);
        publicarE = findViewById(R.id.publicarE);

        autenticacion = FirebaseAuth.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,ubicacionObtenida);

        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapE);
        mapFragment2.getMapAsync(this);
        Log.i("BD","Bandera de actividad :)");
        cuentaE.setOnClickListener(verInfo);
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
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(PrincipalEstudiante.this,ubicacionObtenida);
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                        {
                            Toast.makeText(PrincipalEstudiante.this, "No se ha otorgado el permiso para la ubicacion.", Toast.LENGTH_SHORT).show();
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

    private View.OnClickListener verInfo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent verinfo = new Intent(PrincipalEstudiante.this,InfoEstudiante.class);
            Log.i("BD","Bandera de actividad 2 :)");
            startActivity(verinfo);
        }
    };
}