package com.netteam.unihome.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HolderChats extends RecyclerView.ViewHolder{

    private TextView usuario;

    public HolderChats(@NonNull View itemView) {
        super(itemView);
    }

    public TextView getUsuario() {
        return usuario;
    }

    public void setUsuario(TextView usuario) {
        this.usuario = usuario;
    }
}
