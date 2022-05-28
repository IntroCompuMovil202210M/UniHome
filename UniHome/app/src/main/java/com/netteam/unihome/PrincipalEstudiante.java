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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.netteam.unihome.databinding.ActivityPrincipalBinding;
import com.netteam.unihome.databinding.ActivityPrincipalEstudianteBinding;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PrincipalEstudiante extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityPrincipalEstudianteBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng ubicacion, direccionMarcador;
    private MarkerOptions marcador;
    private ImageButton cuentaE, chatsE, rutaE;
    private FirebaseAuth autenticacion;
    private FirebaseFirestore db;
    private boolean settingsOK;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private SensorManager sensorManager;
    private Sensor lightSensor,tempSensor;
    private SensorEventListener lightSensorListener, tempSensorListener;
    private float tempActual;
    Residencia residencia;
    private Geocoder geocoder;
    private Address resultadoBusqueda;
    private RoadManager roadManager;
    Polyline roadOverlay;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrincipalEstudianteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        marcador = new MarkerOptions();
        ubicacion = new LatLng(0,0);
        settingsOK = false;
        residencia = null;
        geocoder = new Geocoder(this);
        roadManager = new OSRMRoadManager(this, "ANDROID");

        cuentaE = findViewById(R.id.cuentaE);
        chatsE = findViewById(R.id.chatsE);
        rutaE = findViewById(R.id.rutaE);

        autenticacion = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,ubicacionObtenida);
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

        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapE);
        mapFragment2.getMapAsync(this);
        sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(tempSensorListener,tempSensor,sensorManager.SENSOR_DELAY_NORMAL);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        cuentaE.setOnClickListener(verInfo);
        chatsE.setOnClickListener(abrirChat);
        rutaE.setOnClickListener(iniciarRuta);
        rutaE.setActivated(false);
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
        mMap.setOnMarkerClickListener(clickMarcador);
        direccionMarcador = ubicacion;
        rutaE.setActivated(false);
        leerResidencias();
    }

    private GoogleMap.OnMarkerClickListener clickMarcador = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            rutaE.setActivated(true);
            direccionMarcador = marker.getPosition();
            return false;
        }
    };

    private View.OnClickListener iniciarRuta = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(direccionMarcador.latitude!=ubicacion.latitude && direccionMarcador.longitude!=ubicacion.longitude)
            {
                drawRoute(new GeoPoint(ubicacion.latitude,ubicacion.longitude),new GeoPoint(direccionMarcador.latitude,direccionMarcador.longitude));
                direccionMarcador = ubicacion;
            }
        }
    };

    private View.OnClickListener abrirChat = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent actividadChat = new Intent(PrincipalEstudiante.this,ListaChat.class);
            startActivity(actividadChat);
        }
    };

    private void drawRoute(GeoPoint start, GeoPoint finish){
        ArrayList<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(start);
        routePoints.add(finish);
        Road road = roadManager.getRoad(routePoints);
        List<GeoPoint> camino = RoadManager.buildRoadOverlay(road).getActualPoints();
        dibujarRuta(camino);
        BigDecimal distancia = new BigDecimal(road.mLength);
        distancia = distancia.setScale(2, RoundingMode.HALF_UP);
        Toast.makeText(this, "Distancia: "+distancia+" kms.", Toast.LENGTH_SHORT).show();
    }

    private void dibujarRuta(List<GeoPoint> camino){
        com.google.android.gms.maps.model.Polyline ruta;
        PolylineOptions puntosruta = new PolylineOptions();
        ArrayList<LatLng> puntos = new ArrayList<LatLng>();
        for(int i=0;i<camino.size();i++)
        {
            puntos.add(new LatLng(camino.get(i).getLatitude(),camino.get(i).getLongitude()));
        }
        for(int j=0;j<puntos.size();j++)
        {
            puntosruta.add(puntos.get(j));
        }
        ruta = mMap.addPolyline(puntosruta);
        ruta.setColor(0xffff0000);
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
                            Toast.makeText(PrincipalEstudiante.this, result.getResultCode(), Toast.LENGTH_SHORT).show();
                            if(result.getResultCode() == RESULT_OK){
                                settingsOK = true;
                                startLocationUpdates();
                            }else{
                                Toast.makeText(PrincipalEstudiante.this, "GPS Apagado", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PrincipalEstudiante.this, "No hay GPS", Toast.LENGTH_SHORT).show();
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

    private SensorEventListener lecturaSensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(mMap != null)
            {
                if(sensorEvent.values[0]<1500)
                {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PrincipalEstudiante.this, R.raw.modo_oscuro));
                }else{
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(PrincipalEstudiante.this, R.raw.modo_claro));
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
            Intent verinfo = new Intent(PrincipalEstudiante.this,InfoEstudiante.class);
            startActivity(verinfo);
        }
    };

    private void leerResidencias(){
        db.collection("residencias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                residencia = document.toObject(Residencia.class);
                                MarkerOptions marker = new MarkerOptions();
                                LatLng ubicacion = buscarDireccion(residencia.getDireccion());
                                marker.position(ubicacion).title(residencia.getNombre());
                                mMap.addMarker(marker);
                            }
                        } else {
                            Log.i("DB", "Excepción: "+ task.getException());
                        }
                    }
                });
    }

    private LatLng buscarDireccion(String direccion){
        LatLng ubicacionEncontrada = new LatLng(0,0);
        try {
            List<Address> direcciones = geocoder.getFromLocationName(direccion,1);
            if(direcciones != null && !direcciones.isEmpty())
            {
                resultadoBusqueda = direcciones.get(0);
                ubicacionEncontrada = new LatLng(resultadoBusqueda.getLatitude(),resultadoBusqueda.getLongitude());
                return ubicacionEncontrada;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ubicacionEncontrada;
    }
}