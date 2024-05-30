package com.example.primerproyecto;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class MostrarClienteFragment extends Fragment {

    private DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mostrar_cliente, container, false);
        dbHelper = new DbHelper(requireContext());

        mostrarRegistros(view.findViewById(R.id.datosclientes));

        return view;
    }

    private void mostrarRegistros(TextView datosClientesTextView) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query("Usuarios", null, null, null, null, null, null)) {

            StringBuilder clientesText = new StringBuilder();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String apellidos = cursor.getString(cursor.getColumnIndexOrThrow("Apellidos"));
                    String direccion = cursor.getString(cursor.getColumnIndexOrThrow("Direccion"));
                    int telefono = cursor.getInt(cursor.getColumnIndexOrThrow("Telefono"));
                    String correo = cursor.getString(cursor.getColumnIndexOrThrow("Correo_e"));
                    String nif = cursor.getString(cursor.getColumnIndexOrThrow("NIF"));
                    int admin = cursor.getInt(cursor.getColumnIndexOrThrow("Admin"));

                    if (admin == 0) {
                        String clienteInfo = String.format(Locale.getDefault(),
                                "ID: %d\nNombre: %s\nApellidos: %s\nDirección: %s\nTeléfono: %d\nCorreo: %s\nNIF: %s\n\n",
                                id, nombre, apellidos, direccion, telefono, correo, nif);

                        clientesText.append(clienteInfo);
                    }
                } while (cursor.moveToNext());

                datosClientesTextView.setText(clientesText.toString());
            } else {
                datosClientesTextView.setText("No hay clientes registrados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al leer datos", Toast.LENGTH_SHORT).show();
        } finally {
            // Cerrar la conexión a la base de datos después de la operación de lectura
            dbHelper.close();
        }
    }

}
