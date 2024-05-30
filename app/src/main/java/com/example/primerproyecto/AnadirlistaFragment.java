package com.example.primerproyecto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AnadirlistaFragment extends Fragment {

    private EditText nombreEditText, categoriaEditText, enlaceEditText;
    private ImageView imagenlista;
    private Uri imagenUri;
    private DbHelper dbHelper;

    public static AnadirlistaFragment newInstance() {
        return new AnadirlistaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anadir_lista, container, false);

        dbHelper = new DbHelper(requireContext());

        // Initialize views
        nombreEditText = view.findViewById(R.id.Nombre);
        categoriaEditText = view.findViewById(R.id.Categoria);
        enlaceEditText = view.findViewById(R.id.Enlace);
        imagenlista = view.findViewById(R.id.Imagenlista);

        // Button to add a new list
        Button enviarButton = view.findViewById(R.id.Enviar);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement the logic to add a new list to the database
                addlistToDatabase();
            }
        });

        // Button to select an image
        Button seleccionarImagenButton = view.findViewById(R.id.SeleccionarImagen);
        seleccionarImagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    private void addlistToDatabase() {
        String nombre = nombreEditText.getText().toString();
        String categoria = categoriaEditText.getText().toString();
        String enlace = enlaceEditText.getText().toString();

        if (nombre.isEmpty() || categoria.isEmpty() || enlace.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("Nombre", nombre);
                values.put("Categoria", categoria);
                values.put("ENLACE", enlace);
                values.put("Imagen", imagenUri != null ? imagenUri.toString() : "");

                long newRowId = db.insert("Lista", null, values);

                if (newRowId != -1) {
                    Toast.makeText(requireContext(), "Lista agregada correctamente", Toast.LENGTH_SHORT).show();
                    // Clear the fields and the image
                    nombreEditText.setText("");
                    categoriaEditText.setText("");
                    enlaceEditText.setText("");
                    imagenlista.setImageURI(null);
                } else {
                    Toast.makeText(requireContext(), "Error al agregar la lista", Toast.LENGTH_SHORT).show();
                }

                db.close();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            imagenUri = data.getData();
            imagenlista.setImageURI(imagenUri);
        }
    }
}
