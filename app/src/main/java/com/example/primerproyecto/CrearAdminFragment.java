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

public class CrearAdminFragment extends Fragment {

    EditText nombreEditText, apellidosEditText, direccionEditText, telefonoEditText, correoEditText, contraseniaEditText, nifEditText;
    DbHelper dbHelper;

    public CrearAdminFragment() {
        // Required empty public constructor
    }
    public static CrearAdminFragment newInstance() {
        return new CrearAdminFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_admin, container, false);

        dbHelper = new DbHelper(requireContext());

        nombreEditText = view.findViewById(R.id.Nombrecliente);
        apellidosEditText = view.findViewById(R.id.Apellidoscliente);
        direccionEditText = view.findViewById(R.id.Direccioncliente);
        telefonoEditText = view.findViewById(R.id.Telefonocliente);
        correoEditText = view.findViewById(R.id.Correo_ecliente);
        contraseniaEditText = view.findViewById(R.id.Contraseniacliente);
        nifEditText = view.findViewById(R.id.Nifcliente);

        Button enviarButton = view.findViewById(R.id.Enviar);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = nombreEditText.getText().toString();
                String apellidos = apellidosEditText.getText().toString();
                String direccion = direccionEditText.getText().toString();
                String telefonoStr = telefonoEditText.getText().toString();
                String correo = correoEditText.getText().toString();
                String contrasenia = contraseniaEditText.getText().toString();
                String fechaAlta = RegistroActivity.obtenerFechaActual();
                String nif = nifEditText.getText().toString();
                String contraseniaEncriptada = "";

                try {
                    // Encripta la contraseña antes de almacenarla en la base de datos
                    contraseniaEncriptada = encriptar(contrasenia);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (nombre.isEmpty() || apellidos.isEmpty()|| direccion.isEmpty() || telefonoStr.isEmpty() || correo.isEmpty() || contrasenia.isEmpty()|| fechaAlta.isEmpty() || nif.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("Nombre", nombre);
                    values.put("Apellidos", direccion);
                    values.put("Direccion", direccion);
                    values.put("Telefono", Integer.parseInt(telefonoStr));
                    values.put("Correo_e", correo);
                    values.put("Contrasenia", contraseniaEncriptada);
                    values.put("Fecha_alta", fechaAlta);
                    values.put("NIF", nif);
                    values.put("Admin", 1);

                    long newRowId = db.insert("Usuarios", null, values);

                    if (newRowId != -1) {
                        Toast.makeText(requireContext(), "Admin agregado correctamente", Toast.LENGTH_SHORT).show();
                        // Limpia los campos
                        nombreEditText.setText("");
                        apellidosEditText.setText("");
                        direccionEditText.setText("");
                        telefonoEditText.setText("");
                        correoEditText.setText("");
                        contraseniaEditText.setText("");
                        nifEditText.setText("");
                    } else {
                        Toast.makeText(requireContext(), "Error al agregar el admin", Toast.LENGTH_SHORT).show();
                    }

                    db.close();
                }
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
