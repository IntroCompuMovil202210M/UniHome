package com.netteam.unihome.models;

public class Mensaje {
    private String msg;
    private String nombre;
    private String hora;
    private Integer pos;

    public Mensaje(){

    }

    public Mensaje(String msg, String nombre, String hora, Integer pos) {
        this.msg = msg;
        this.nombre = nombre;
        this.hora = hora;
        this.pos = pos;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHora() {
        return hora;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
