package com.example.primerproyecto;



import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ModificarClienteFragment extends Fragment {

    EditText idEditText, nombreEditText, apellidosEditText, direccionEditText, telefonoEditText, correoEditText, contraseniaEditText, nifEditText;
    DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_modificar_cliente, container, false);

        dbHelper = new DbHelper(requireContext());

        idEditText = view.findViewById(R.id.Idcliente);
        nombreEditText = view.findViewById(R.id.Nombrecliente);
        apellidosEditText = view.findViewById(R.id.Apellidoscliente);
        direccionEditText = view.findViewById(R.id.Direccioncliente);
        telefonoEditText = view.findViewById(R.id.Telefonocliente);
        correoEditText = view.findViewById(R.id.Correo_ecliente);
        contraseniaEditText = view.findViewById(R.id.Contraseniacliente);
        nifEditText = view.findViewById(R.id.Nifcliente);

        Button modificarButton = view.findViewById(R.id.Enviar);
        modificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
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
                String[] selectionArgs = {id};

                int rowsUpdated = db.update("Usuarios", values, selection, selectionArgs);

                if (rowsUpdated > 0) {
                    Toast.makeText(getContext(), "Cliente modificado correctamente", Toast.LENGTH_SHORT).show();
                    // Puedes redirigir al usuario a otra actividad o realizar otras acciones aquí
                } else {
                    Toast.makeText(getContext(), "Error al modificar cliente", Toast.LENGTH_SHORT).show();
                }

                db.close();
            }
        });
        return view;
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