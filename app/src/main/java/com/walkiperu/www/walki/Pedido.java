package com.walkiperu.www.walki;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class Pedido implements Serializable {

    private String codigo;
    private String uidUser;
    private String nombre;
    private Ubicacion origen;
    private String adressOrigen;
    private Ubicacion destino;
    private String adressDestino;
    private String urlFotoUser;
    private double scoreUser;
    private String  uid;
    private String fecha;
    private double precio;
    private String comentario;
    private int polarizado;
    private int visa;
    private int aireAcondicionado;
    private int mas4Pasajeros;

    private String distancia;
    private String tiempo;
    private ArrayList<LatLng> points;

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<LatLng> points) {
        this.points = points;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getAdressOrigen() {
        return adressOrigen;
    }

    public void setAdressOrigen(String adressOrigen) {
        this.adressOrigen = adressOrigen;
    }

    public String getAdressDestino() {
        return adressDestino;
    }

    public void setAdressDestino(String adressDestino) {
        this.adressDestino = adressDestino;
    }

    public String getUrlFotoUser() {
        return urlFotoUser;
    }

    public void setUrlFotoUser(String urlFotoUser) {
        this.urlFotoUser = urlFotoUser;
    }

    public double getScoreUser() {
        return scoreUser;
    }

    public void setScoreUser(double scoreUser) {
        this.scoreUser = scoreUser;
    }

    public int getMas4Pasajeros() {
        return mas4Pasajeros;
    }

    public void setMas4Pasajeros(int mas4Pasajeros) {
        this.mas4Pasajeros = mas4Pasajeros;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Ubicacion getOrigen() {
        return origen;
    }

    public void setOrigen(Ubicacion origen) {
        this.origen = origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public void setDestino(Ubicacion destino) {
        this.destino = destino;
    }



    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getPolarizado() {
        return polarizado;
    }

    public void setPolarizado(int polarizado) {
        this.polarizado = polarizado;
    }

    public int getVisa() {
        return visa;
    }

    public void setVisa(int visa) {
        this.visa = visa;
    }

    public int getAireAcondicionado() {
        return aireAcondicionado;
    }

    public void setAireAcondicionado(int aireAcondicionado) {
        this.aireAcondicionado = aireAcondicionado;
    }




}
