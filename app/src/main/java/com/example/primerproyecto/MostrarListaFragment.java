package com.example.primerproyecto;


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

public class MostrarListaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListaAdapter listaAdapter;

    public MostrarListaFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_listas, container, false);

        recyclerView = view.findViewById(R.id.recyclerListas);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (listaAdapter == null) {
            listaAdapter = new ListaAdapter(requireContext());
        }

        recyclerView.setAdapter(listaAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String categoriaSeleccionada = bundle.getString("Categoria");
            if ("Ver todo".equals(categoriaSeleccionada)) {
                cargarTodasLasListas();
            } else {
                cargarListasPorCategoria(categoriaSeleccionada);
            }
        } else {
            cargarTodasLasListas();
        }
        return view;
    }

    private void cargarListasPorCategoria(String categoria) {
        DbHelper dbHelper = new DbHelper(requireContext());
        List<Lista> listas = obtenerListasPorCategoria(dbHelper, categoria);

        listaAdapter.setListas(listas);
        listaAdapter.notifyDataSetChanged();
    }

    private List<Lista> obtenerListasPorCategoria(DbHelper dbHelper, String categoria) {
        List<Lista> listas = new ArrayList<>();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query("Lista", null, "Categoria=?", new String[]{categoria}, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String enlace = cursor.getString(cursor.getColumnIndexOrThrow("ENLACE"));
                    String imagenPath = cursor.getString(cursor.getColumnIndexOrThrow("Imagen"));

                    Lista lista = new Lista(id, nombre, categoria, enlace, imagenPath);
                    listas.add(lista);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }

        return listas;
    }

    private void cargarTodasLasListas() {
        DbHelper dbHelper = new DbHelper(requireContext());
        List<Lista> listas = obtenerTodasLasListas(dbHelper);

        listaAdapter.setListas(listas);
        listaAdapter.notifyDataSetChanged();
    }

    private List<Lista> obtenerTodasLasListas(DbHelper dbHelper) {
        List<Lista> listas = new ArrayList<>();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query("Lista", null, null, null, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow("Categoria"));
                    String enlace = cursor.getString(cursor.getColumnIndexOrThrow("ENLACE"));
                    String imagenPath = cursor.getString(cursor.getColumnIndexOrThrow("Imagen"));

                    Lista lista = new Lista(id, nombre, categoria, enlace, imagenPath);
                    listas.add(lista);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }

        return listas;
    }
}
