package com.netteam.unihome.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netteam.unihome.ChatActivity;
import com.netteam.unihome.R;
import com.netteam.unihome.holders.HolderMensaje;
import com.netteam.unihome.models.Mensaje;

import java.util.ArrayList;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<HolderMensaje> {

    private List<Mensaje> ListMensaje = new ArrayList<>();
    private Context c;

    public MensajeAdapter(Context c) {
        this.c = c;
    }

    public void addMensaje(Mensaje m){
        ListMensaje.add(m);
        notifyItemInserted(ListMensaje.size());
    }

    @NonNull
    @Override
    public HolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_contenedor_mensaje_enviado,parent,false);
        return new HolderMensaje(v);
    }

    @Override
    public void onBindViewHolder(HolderMensaje holder, int position) {
        holder.getNombre().setText(ListMensaje.get(position).getNombre());
        holder.getMensaje().setText(ListMensaje.get(position).getMsg());
        holder.getHora().setText(ListMensaje.get(position).getHora());
    }

    @Override
    public int getItemCount() {
        return ListMensaje.size();
    }
}
