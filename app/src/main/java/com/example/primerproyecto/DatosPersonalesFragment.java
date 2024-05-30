package com.example.primerproyecto;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DatosPersonalesFragment extends Fragment {

    EditText  nombreEditText, apellidosEditText, direccionEditText, telefonoEditText, correoEditText, contraseniaEditText, nifEditText;
    DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_datos_personales, container, false);

        dbHelper = new DbHelper(requireContext());

        nombreEditText = view.findViewById(R.id.Nombreusuario);
        apellidosEditText = view.findViewById(R.id.Apellidosusuario);
        direccionEditText = view.findViewById(R.id.Direccionusuario);
        telefonoEditText = view.findViewById(R.id.Telefonousuario);
        correoEditText = view.findViewById(R.id.Correo_eusuario);
        contraseniaEditText = view.findViewById(R.id.Contraseniausuario);
        nifEditText = view.findViewById(R.id.Nifusuario);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);

        Button modificarButton = view.findViewById(R.id.Enviar);
        modificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = nombreEditText.getText().toString();
                String apellidos = apellidosEditText.getText().toString();
                String direccion = direccionEditText.getText().toString();
                String telefono = telefonoEditText.getText().toString();
                String correo = correoEditText.getText().toString();
                String contrasenia = contraseniaEditText.getText().toString();
                String nif = nifEditText.getText().toString();
                String contraseniaEncriptada = "";

                try {
                    // Encripta la contraseña antes de almacenarla en la base de datos
                    contraseniaEncriptada = encriptar(contrasenia);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                if (!nombre.isEmpty()) {
                    values.put("Nombre", nombre);
                }
                if (!apellidos.isEmpty()) {
                    values.put("Apellidos", apellidos);
                }
                if (!direccion.isEmpty()) {
                    values.put("Direccion", direccion);
                }
                if (!telefono.isEmpty()) {
                    values.put("Telefono", telefono);
                }
                if (!correo.isEmpty()) {
                    values.put("Correo_e", correo);
                }
                if (!contrasenia.isEmpty()) {
                    values.put("Contrasenia", contraseniaEncriptada);
                }
                if (!nif.isEmpty()) {
                    values.put("NIF", nif);
                }

                String selection = "id = ?";
                String[] selectionArgs = {String.valueOf(idUsuario)};

                int rowsUpdated = db.update("Usuarios", values, selection, selectionArgs);

                if (rowsUpdated > 0) {
                    Toast.makeText(getContext(), "Datos modificados correctamente", Toast.LENGTH_SHORT).show();

                    String nuevoNombre = obtenerNombreUsuario(idUsuario);
                    String nuevoCorreo = obtenerCorreoUsuario(idUsuario);

                    // Actualiza SharedPreferences con la nueva información
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nombreUsuario", nuevoNombre);
                    editor.putString("correoUsuario", nuevoCorreo);
                    editor.apply();
                    TextView nombreTextView = getActivity().findViewById(R.id.nombreusuario);
                    TextView correoTextView = getActivity().findViewById(R.id.gmailusuario);

                    // Actualizar los TextView con los nuevos datos
                    nombreTextView.setText(nuevoNombre);
                    correoTextView.setText(nuevoCorreo);
                } else {
                    Toast.makeText(getContext(), "Error al modificar datos", Toast.LENGTH_SHORT).show();
                }

                db.close();
            }
        });
        return view;
    }
    @SuppressLint("Range")
    public String obtenerNombreUsuario(long idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nombreUsuario = "";

        Cursor cursor = db.rawQuery("SELECT Nombre FROM Usuarios WHERE id = ?",
                new String[]{String.valueOf(idUsuario)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                nombreUsuario = cursor.getString(cursor.getColumnIndex("Nombre"));
            }
            cursor.close();
        }

        return nombreUsuario;
    }
    @SuppressLint("Range")
    public String obtenerCorreoUsuario(long idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String correoUsuario = "";

        Cursor cursor = db.rawQuery("SELECT Correo_e FROM Usuarios WHERE id = ?",
                new String[]{String.valueOf(idUsuario)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                correoUsuario = cursor.getString(cursor.getColumnIndex("Correo_e"));
            }
            cursor.close();
        }
        return correoUsuario;
    }
    protected String encriptar(String contrasenia) throws Exception {
        // Utiliza una clave secreta segura
        String claveSecreta = "tu_clave_secreta";
        SecretKeySpec secretKey = generateKey(claveSecreta);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(contrasenia.getBytes());
            return Base64.getEncoder().encodeToString(datosEncriptadosBytes);

    }
    private static SecretKeySpec generateKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        return new SecretKeySpec(key, "AES");
    }
}
