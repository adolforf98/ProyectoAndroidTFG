package com.example.primerproyecto;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class BorrarClienteFragment extends Fragment {

    EditText idEditText;
    DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrar_cliente, container, false);

        dbHelper = new DbHelper(requireContext());
        idEditText = view.findViewById(R.id.ideliminar);

        Button eliminarButton = view.findViewById(R.id.Enviareliminar);
        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el valor del EditText (ID)
                String id = idEditText.getText().toString();

                // Validar que el campo no esté vacío
                if (id.isEmpty()) {
                    Toast.makeText(getActivity(), "Por favor, ingresa un ID válido", Toast.LENGTH_SHORT).show();
                } else {
                    // Obtener una instancia de SQLiteDatabase
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    // Definir la cláusula WHERE para la eliminación
                    String selection = "id = ?";
                    String[] selectionArgs = {id};

                    // Realizar la eliminación de los datos
                    int rowsDeleted = db.delete("Usuarios", selection, selectionArgs);

                    // Verificar si la eliminación fue exitosa
                    if (rowsDeleted > 0) {
                        // Muestra un mensaje de éxito
                        Toast.makeText(getActivity(), "Cliente eliminado correctamente", Toast.LENGTH_SHORT).show();

                        // Limpia el campo después de eliminar el producto
                        idEditText.setText("");
                    } else {
                        Toast.makeText(getActivity(), "Error al eliminar el cliente", Toast.LENGTH_SHORT).show();
                    }

                    // Cierra la base de datos
                    db.close();
                }
            }
        });
        return view;
    }
}
