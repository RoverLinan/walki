package com.walkiperu.www.walki;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import firebase.connection.FirebaseConn;

public class Principal extends Activity {

    private final   int WELCOME_TIME = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        if(FirebaseConn.firebaseAuth == null){
            FirebaseConn.iniciarConn();
        }



    }


    @Override
    protected void onStart() {
        super.onStart();
        setCountry();


        if(DirectionsUtility.country != null ){

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseUser user = FirebaseConn.firebaseAuth.getCurrentUser();

                    if(user != null) {

                        FirebaseConn.firebaseAuth.signInWithEmailAndPassword(user.getEmail(),getPhone(user.getEmail())).addOnCompleteListener(Principal.this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Intent lanzador = null;

                                if(task.isSuccessful()){
                                    lanzador =  new Intent(getBaseContext(),UserMain.class);
                                }else{
                                    lanzador = new Intent(getBaseContext(),LoginActivity.class);

                                }
                                startActivity(lanzador);
                                finish();
                            }
                        });
                    }else{

                        Intent lanzador = new Intent(getBaseContext(),LoginActivity.class);
                        startActivity(lanzador);
                        finish();
                    }


                }
            },WELCOME_TIME);
        }


    }



    private  void setCountry(){

        LatLng ubication = ubicationUser();

        if(ubication != null){
            getAddressFromLocation(ubication.latitude,ubication.longitude);
        }else{
            Toast.makeText(this, "GPS:  problemas para obtener su ubicacion", Toast.LENGTH_SHORT).show();
        }

    }





    private void getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());


        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {

                DirectionsUtility.country = addresses.get(0).getCountryName();
                DirectionsUtility.adminArea = addresses.get(0).getAdminArea();
            } else {
                DirectionsUtility.country = null;
                DirectionsUtility.adminArea = null;
            }

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private LatLng ubicationUser() {

        LocationManager locationManager = getSystemService(LocationManager.class);

        String provider = locationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        return  latLng;

    }



    private String getPhone(String text){

        int index = 0;

        while(text.charAt(index) != '@'){

            index++;
        }

        char[] cadena = new  char[index];

        index = 0;

        while(text.charAt(index) != '@'){
            cadena[index] = text.charAt(index);

            index++;
        }


        return   new String(cadena);

    }


}


