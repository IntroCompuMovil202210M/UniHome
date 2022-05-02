package com.netteam.unihome;

public class Estudiante {
    private String nombre;
    private String apellido;
    private String universidad;
    private String programa;
    //Acá va el atributo del recibo de matricula.
    //Acá va la foto.


    public Estudiante(String nombre, String apellido, String universidad, String programa) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.universidad = universidad;
        this.programa = programa;
    }
}
