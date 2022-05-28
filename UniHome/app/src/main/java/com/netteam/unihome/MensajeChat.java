package com.netteam.unihome;

public class MensajeChat {

    private String chatMensaje;
    private String usuario;
    private long tiempoDeMensaje;

    public MensajeChat(String chatMensaje,String usuario){
        this.chatMensaje = chatMensaje;
        this.usuario = usuario;
    }

    public String getChatMensaje() {
        return chatMensaje;
    }

    public long getTiempoDeMensaje() {
        return tiempoDeMensaje;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setChatMensaje(String chatMensaje) {
        this.chatMensaje = chatMensaje;
    }

    public void setTiempoDeMensaje(long tiempoDeMensaje) {
        this.tiempoDeMensaje = tiempoDeMensaje;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
