package com.example.primerproyecto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ListaViewHolder> {

    private static List<Lista> listas;
    public long idUsuario;
    private SharedPreferences prefs;
    private MostrarListaFragment listasFragment;

    public ListaAdapter(Context context) {
        prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        idUsuario = prefs.getLong("idUsuario", -1);
    }

    public void setMostrarListaFragment(MostrarListaFragment listasFragment) {
        this.listasFragment = listasFragment;
    }

    public void setListas(List<Lista> listas) {
        this.listas = listas;
        notifyDataSetChanged();
    }

    public void clearListas() {
        if (listas != null) {
            listas.clear();
            notifyDataSetChanged();
        }
    }

    public void addListas(List<Lista> nuevosListas) {
        if (listas == null) {
            listas = new ArrayList<>();
        }
        listas.addAll(nuevosListas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new ListaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaViewHolder holder, int position) {
        Lista lista = listas.get(position);
        holder.nombreTextView.setText(lista.getNombre());

        if (isListaEnListaFavoritos(lista.getId(), holder.itemView.getContext())) {
            holder.faviconImageView.setImageResource(R.drawable.fullfav);
        } else {
            holder.faviconImageView.setImageResource(R.drawable.emptyfav);
        }

        Glide.with(holder.itemView.getContext())
                .load(lista.getImagen())
                .into(holder.imagenImageView);

        holder.imagenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lista.getEnlace()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    private boolean isListaEnListaFavoritos(long idLista, Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ListasLike WHERE idUsuario = ? AND idLista = ?",
                new String[]{String.valueOf(idUsuario), String.valueOf(idLista)});

        boolean listaEnLista = false;
        if (cursor != null) {
            listaEnLista = cursor.getCount() > 0;
            cursor.close();
        }
        db.close();
        return listaEnLista;
    }

    @Override
    public int getItemCount() {
        return listas != null ? listas.size() : 0;
    }

    public class ListaViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenImageView;
        TextView nombreTextView;
        ImageView faviconImageView;

        public ListaViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenImageView = itemView.findViewById(R.id.imagenImageView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            faviconImageView = itemView.findViewById(R.id.favicon);

            faviconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agregarAFavoritos(listas.get(getAdapterPosition()).getId());
                }
            });
        }

        private void agregarAFavoritos(long idLista) {
            boolean listaEnLista = isListaEnListaFavoritos(idLista, itemView.getContext());

            if (!listaEnLista) {
                faviconImageView.setImageResource(R.drawable.fullfav);
                DbHelper dbHelper = new DbHelper(itemView.getContext());
                dbHelper.agregarListaDeseado(idUsuario, idLista);
                Toast.makeText(itemView.getContext(), "Lista añadida a la lista de likes", Toast.LENGTH_SHORT).show();
            } else {
                faviconImageView.setImageResource(R.drawable.emptyfav);
                DbHelper dbHelper = new DbHelper(itemView.getContext());
                dbHelper.eliminarListaDeseado(idUsuario, idLista);
                Toast.makeText(itemView.getContext(), "Lista eliminada de la lista de likes", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
