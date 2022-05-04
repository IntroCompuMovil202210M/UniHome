package com.netteam.unihome;

public class Estudiante {
    private String nombre;
    private String apellido;
    private String universidad;
    private String programa;
    //Acá va el atributo del recibo de matricula.
    //Acá va la foto.

    public Estudiante(){
        this.nombre = "";
        this.apellido = "";
        this.universidad = "";
        this.programa = "";
    }

    public Estudiante(String nombre, String apellido, String universidad, String programa) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.universidad = universidad;
        this.programa = programa;
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

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }
}
