package com.walkiperu.www.walki;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.getSystemService;

public class SelectorAdressMap extends AppCompatActivity {

    private TextView locationAdress;
    private TextView message;
    private ImageView img_ubicacion;

    private LatLng center;
    private Ubicacion puntoMapa;
    private Location miUbicacion;



    private int selecMode;
    private final int SELECT_ADRESS = 1;
    private  final int SELECT_SALIDA = 0;
    private final int SELECT_DESTINO = 1;

    private SupportMapFragment mapFragment;
    private GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_selector_adress);

        locationAdress = findViewById(R.id.tv_adress);
        img_ubicacion = findViewById(R.id.img_ubicacion);

        message = findViewById(R.id.tv_message_sadress);

        selecMode = (int)getIntent().getSerializableExtra("mode");
        message.setText((String) getIntent().getSerializableExtra("mensaje"));
        if(selecMode == SELECT_DESTINO) {
            img_ubicacion.setImageResource(R.drawable.ic_point_destino_svg);

        }else if(selecMode == SELECT_SALIDA){
            img_ubicacion.setImageResource(R.drawable.ic_point_salida_svg);
        }
        miUbicacion();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_select_adress);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;
                map.setMyLocationEnabled(true);
                LatLng latLng = new LatLng(miUbicacion.getLatitude(),miUbicacion.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                initCameraIdle();

            }
        });

    }


    private void initCameraIdle() {
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                center = map.getCameraPosition().target;
                getAddressFromLocation(center.latitude, center.longitude);
            }
        });
    }


    private void getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());


        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {

                String adress = addresses.get(0).getAddressLine(0);
                puntoMapa = new Ubicacion();
                puntoMapa.setAdresss(adress);
                puntoMapa.setLatitud(latitude);
                puntoMapa.setLogitud(longitude);

                locationAdress.setText("Direccion:  " + adress + "  "  + addresses.get(0).getCountryName() + " -- "+ addresses.get(0).getSubLocality()  +"  --- " +addresses.get(0).getLocality() +" --- " + addresses.get(0).getAdminArea());

            } else {
                locationAdress.setText("Direccion no encontrada");
            }

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    public void returnData(View view){

        Intent intent = new Intent(this,UserMain.class);

        intent.putExtra("dato",puntoMapa);
        intent.putExtra("mode",selecMode);
        setResult(RESULT_OK,intent);
        finish();
    }


    private void miUbicacion() {

        LocationManager locationManager = getSystemService(LocationManager.class);

        String provider = locationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        miUbicacion = locationManager.getLastKnownLocation(provider);

    }
}