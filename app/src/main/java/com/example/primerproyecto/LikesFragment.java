package com.example.primerproyecto;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListaAdapter listaAdapter;
    private long idUsuario;

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        recyclerView = view.findViewById(R.id.recyclerlistas);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (listaAdapter == null) {
            listaAdapter = new ListaAdapter(requireContext());
        }

        recyclerView.setAdapter(listaAdapter);

        SharedPreferences prefs = requireContext().getSharedPreferences("MisPreferencias", requireContext().MODE_PRIVATE);
        idUsuario = prefs.getLong("idUsuario", -1);

        if (idUsuario != -1) {
            cargarDeseados();
        } else {
            // Manejar la situación en la que no se puede obtener el idUsuario desde SharedPreferences
            // (por ejemplo, redirigir a la pantalla de inicio de sesión)
        }


        return view;
    }

    private void cargarDeseados() {
        // Modifica la lógica para obtener todos los productos de la base de datos
        DbHelper dbHelper = new DbHelper(requireContext());
        List<Lista> listas = obtenerListasLikeadas(dbHelper, idUsuario);

        // Agregar los productos al adaptador
        listaAdapter.setListas(listas);
        listaAdapter.notifyDataSetChanged();
    }
    private List<Lista> obtenerListasLikeadas(DbHelper dbHelper, long idUsuario) {
        List<Lista> listas = new ArrayList<>();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT l.* FROM ListasLikeadas ll " +
                     "JOIN Lista l ON ll.idLista = l.id " +
                     "WHERE ll.idUsuario = ?", new String[]{String.valueOf(idUsuario)})) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Obtener datos de la lista deseada
                    long idLista = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow("Categoria"));
                    String enlace = cursor.getString(cursor.getColumnIndexOrThrow("ENLACE"));
                    String imagenPath = cursor.getString(cursor.getColumnIndexOrThrow("Imagen"));

                    // Crear un objeto Lista y agregarlo a la lista de resultados
                    Lista lista = new Lista(idLista, nombre, categoria, enlace, imagenPath);
                    listas.add(lista);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Manejar error al obtener los registros
        }

        return listas;
    }

}
