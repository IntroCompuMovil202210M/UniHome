package com.netteam.unihome;

import android.net.Uri;

public class Residencia {

    private String nombre, descripcion, direccion, arrendatario,foto;


    public Residencia(){
        this.nombre = "";
        this.descripcion = "";
        this.direccion = "";
        this.arrendatario = "";
        this.foto = null;
    }

    public Residencia(String nombre, String descripcion, String direccion, String arrendatario, String foto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.arrendatario = arrendatario;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getArrendatario() {
        return arrendatario;
    }

    public void setArrendatario(String arrendatario) {
        this.arrendatario = arrendatario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
