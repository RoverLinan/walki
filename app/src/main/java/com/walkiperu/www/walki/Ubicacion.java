package com.walkiperu.www.walki;

import java.io.Serializable;

public class Ubicacion implements Serializable {

    private double latitud;
    private double logitud;

    private String adresss;
    private String subLocality;
    private String locality;
    private String status;
    private String country;

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLogitud() {
        return logitud;
    }

    public void setLogitud(double logitud) {
        this.logitud = logitud;
    }

    public String getAdresss() {
        return adresss;
    }

    public void setAdresss(String adresss) {
        this.adresss = adresss;
    }
}
