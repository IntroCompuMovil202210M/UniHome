package com.netteam.unihome.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netteam.unihome.R;

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView nombre,mensaje, hora;

    public HolderMensaje(View itemView) {
        super(itemView);

        nombre = (TextView) itemView.findViewById(R.id.nombreMensajeChat);
        mensaje = (TextView) itemView.findViewById(R.id.textoMensaje);
        hora = (TextView) itemView.findViewById(R.id.textDateTime);
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }
}
