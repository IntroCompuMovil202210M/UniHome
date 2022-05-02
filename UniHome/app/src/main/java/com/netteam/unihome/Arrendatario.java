package com.netteam.unihome;

import java.util.ArrayList;

public class Arrendatario {
    private String nombre;
    private String apellido;
    //Atributo de la foto.

    public Arrendatario(){
        nombre="";
        apellido="";
    }


    public Arrendatario(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
