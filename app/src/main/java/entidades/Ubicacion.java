package entidades;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Ubicacion  implements Serializable {

    private String country;
    private String city;
    private LatLng gps;

}
