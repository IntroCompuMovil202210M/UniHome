package com.netteam.unihome.models;

public class Chat {

    private String estudiante;
    private String arrendatario;

    public Chat(String estudiante, String arrendatario) {
        this.estudiante = estudiante;
        this.arrendatario = arrendatario;
    }

    public  Chat(){

    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getArrendatario() {
        return arrendatario;
    }

    public void setArrendatario(String arrendatario) {
        this.arrendatario = arrendatario;
    }
}
