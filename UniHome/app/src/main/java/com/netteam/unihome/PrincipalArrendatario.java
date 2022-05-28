package com.netteam.unihome;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
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
    private ImageButton cuentaA,chatsA,publicarA;
    private FirebaseAuth autenticacion;
    private boolean settingsOK;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private SensorManager sensorManager;
    private Sensor lightSensor,tempSensor;
    private SensorEventListener lightSensorListener, tempSensorListener;
    private float tempActual;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marcador = new MarkerOptions();
        ubicacion = new LatLng(0,0);
        settingsOK = false;

        autenticacion = FirebaseAuth.getInstance();

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cuentaA = findViewById(R.id.cuentaA);
        chatsA = findViewById(R.id.chatsA);
        publicarA = findViewById(R.id.publicarA);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,ubicacionObtenida);
        mLocationRequest = createLocationRequest();
        mLocationCallback = callbackUbicacion;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        lightSensorListener = lecturaSensor;
        tempSensorListener = lecturaTemperatura;
        tempActual = 0;
        checkLocationSettings();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapA);
        mapFragment.getMapAsync(this);
        sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(tempSensorListener,tempSensor,sensorManager.SENSOR_DELAY_NORMAL);
        cuentaA.setOnClickListener(verInfo);
        publicarA.setOnClickListener(publicar);
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

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        sensorManager.registerListener(lightSensorListener,lightSensor,sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(tempSensorListener,tempSensor,sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        sensorManager.unregisterListener(lightSensorListener);
        sensorManager.unregisterListener(tempSensorListener);
    }

    ActivityResultLauncher<IntentSenderRequest> getLocationSettings =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Toast.makeText(PrincipalArrendatario.this, result.getResultCode(), Toast.LENGTH_SHORT).show();
                            if(result.getResultCode() == RESULT_OK){
                                settingsOK = true;
                                startLocationUpdates();
                            }else{
                                Toast.makeText(PrincipalArrendatario.this, "GPS Apagado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    private void checkLocationSettings(){
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                settingsOK = true;
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(((ApiException) e).getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest isr = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    getLocationSettings.launch(isr);
                }else {
                    Toast.makeText(PrincipalArrendatario.this, "No hay GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private LocationCallback callbackUbicacion = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location nuevaUbicacion = locationResult.getLastLocation();
            ubicacion = new LatLng(nuevaUbicacion.getLatitude(),nuevaUbicacion.getLongitude());
            marcador.position(ubicacion).title("Ubicacion Actual");
            Log.i("DB","Ubicación Actualizada!");
            /*if(nuevaUbicacion!=null)
            {
                if(distance(ubicacion.latitude,ubicacion.longitude,
                        nuevaUbicacion.getLatitude(),nuevaUbicacion.getLongitude()) >= 0.03)
                {
                    writeJSONObject();
                    ubicacion = new LatLng(nuevaUbicacion.getLatitude(),nuevaUbicacion.getLongitude());
                    marcador.position(ubicacion).title("Ubicacion Actual");
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
                }else{
                    ubicacion = new LatLng(nuevaUbicacion.getLatitude(),nuevaUbicacion.getLongitude());
                    marcador.position(ubicacion).title("Ubicacion Actual");
                }

            }*/
        }
    };


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

    private SensorEventListener lecturaSensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(mMap != null)
            {
                if(sensorEvent.values[0]<1500)
                {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PrincipalArrendatario.this, R.raw.modo_oscuro));
                }else{
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PrincipalArrendatario.this, R.raw.modo_claro));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private SensorEventListener lecturaTemperatura = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(mMap!=null){
                if(Math.abs(tempActual-sensorEvent.values[0])>10){
                    tempActual = sensorEvent.values[0];
                    if(tempActual<12)
                    {
                        Log.i("SENSOR","La temperatura es muy baja, abríguese mijo!");
                    }else if(tempActual>25) {
                        Log.i("SENSOR","La temperatura es alta, toma agüita!");
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private View.OnClickListener verInfo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent verinfo = new Intent(PrincipalArrendatario.this,InfoArrendatario.class);
            startActivity(verinfo);
        }
    };

    private View.OnClickListener publicar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent publicar = new Intent(PrincipalArrendatario.this,Publicar.class);
            startActivity(publicar);
        }
    };
}