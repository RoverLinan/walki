package com.walkiperu.www.walki;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DirectionsUtility {

    public static String country;
    public static String adminArea;


    public static String getSubDirections(String direction){


        int index = 0;

        while(direction.charAt(index) != ','){

            index++;
        }

        char[] cadena = new  char[index];

        index = 0;

        while(direction.charAt(index) != ','){
            cadena[index] = direction.charAt(index);

            index++;
        }


        return   new String(cadena);




    }



    public static double getDistance(LatLng origen, LatLng  destino) {

        Location location1 = new Location("gps");

        location1.setLatitude(origen.latitude);
        location1.setLongitude(origen.longitude);

        Location location2 = new Location("gps");
        location2.setLongitude(destino.longitude);
        location2.setLatitude(destino.latitude);


        double metros = 0;
        metros = location1.distanceTo(location2);

        return metros;
    }
}
