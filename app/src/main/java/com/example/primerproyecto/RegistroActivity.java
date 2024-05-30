package com.example.primerproyecto;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RegistroActivity extends AppCompatActivity {
    DbHelper dbHelper;
    EditText editTextNombre, editTextApellidos, editTextDireccion, editTextTelefono, editTextCorreo,editTextContrasenia, editTextNIF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        editTextNombre = findViewById(R.id.Nombre);
        editTextApellidos = findViewById(R.id.Apellidos);
        editTextDireccion = findViewById(R.id.Direccion);
        editTextTelefono = findViewById(R.id.Telefono);
        editTextCorreo = findViewById(R.id.Correo_e);
        editTextContrasenia = findViewById(R.id.Contrasenia);
        editTextNIF = findViewById(R.id.NIF);

        dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Button enviarButton = findViewById(R.id.Enviar);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = editTextNombre.getText().toString();
                String apellidos = editTextApellidos.getText().toString();
                String direccion = editTextDireccion.getText().toString();
                int telefono = Integer.parseInt(editTextTelefono.getText().toString());
                String correo = editTextCorreo.getText().toString();
                String contrasenia = editTextContrasenia.getText().toString();
                String contraseniaEncriptada = "";

                try {
                    // Encripta la contraseña antes de almacenarla en la base de datos
                    contraseniaEncriptada = encriptar(contrasenia);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String fechaAlta = obtenerFechaActual(); // Implementa esta función
                String nif = editTextNIF.getText().toString();
                int admin = 0; // Siempre será 0 para clientes

                // Inserta los datos en la tabla "Usuarios"
                ContentValues values = new ContentValues();
                values.put("Nombre", nombre);
                values.put("Apellidos", apellidos);
                values.put("Direccion", direccion);
                values.put("Telefono", telefono);
                values.put("Correo_e", correo);
                values.put("Contrasenia", contraseniaEncriptada);
                values.put("Fecha_alta", fechaAlta);
                values.put("NIF", nif);
                values.put("Admin", admin);

                long newRowId = db.insert("Usuarios", null, values);

// Cierra la conexión a la base de datos
                db.close();
                if (newRowId != -1) {
                    Toast.makeText(RegistroActivity.this, "registrado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegistroActivity.this, "error al registrar", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public static String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = new Date();
        return dateFormat.format(fechaActual);
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

