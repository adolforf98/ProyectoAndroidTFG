package com.example.primerproyecto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    EditText correoEditText, contraseniaEditText;
    DbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView Registro = findViewById(R.id.Registro);
        TextView Invitado = findViewById(R.id.Invitado);
        Button iniciarsesion = findViewById(R.id.iniciarsesion);
        dbHelper = new DbHelper(this);
        correoEditText = findViewById(R.id.correo_elogin);
        contraseniaEditText = findViewById(R.id.Contrasenialogin);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        // Verificar si hay un ID de usuario almacenado en las preferencias compartidas
        long idUsuarioAlmacenado = prefs.getLong("idUsuario", -1);
        if (idUsuarioAlmacenado != -1) {
            irAUsuarioActivity(idUsuarioAlmacenado, 0);
        }

        File archivo = new File("/data/data/com.example.primerproyecto/databases/ondas.db");
        if (archivo.exists() || dbHelper.verificaExistenciaBaseDatos()) {
            Toast.makeText(MainActivity.this, "CONECTADO A LA BASE DE DATOS", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "BASE DE DATOS CREADA", Toast.LENGTH_LONG).show();
        }

        Registro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        Invitado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InvitadoActivity.class);
                startActivity(intent);
            }
        });

        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = dbHelper.getReadableDatabase();
                String correo = correoEditText.getText().toString();
                String contrasenia = contraseniaEditText.getText().toString();

                long idUsuario = obtenerIdUsuario(correo); // Obten el ID del usuario
                int admin = validarUsuario(correo, contrasenia);

                if (admin == 0) {
                    irAUsuarioActivity(idUsuario, admin);
                } else if (admin == 1) {
                    irAAdminActivity(idUsuario, 1);
                }
                db.close();
            }
        });
    }

    private int validarUsuario(String correo, String contrasenia) {
        try {
            // Encripta la contraseña ingresada por el usuario
            String contraseniaEncriptada = encriptar(contrasenia);

            Cursor cursor = db.rawQuery("SELECT * FROM Usuarios WHERE Correo_e = ? AND Contrasenia = ?",
                    new String[]{correo, contraseniaEncriptada});

            if (cursor != null && cursor.moveToFirst()) {
                int adminColumnIndex = cursor.getColumnIndex("Admin");

                if (adminColumnIndex != -1) {
                    int admin = cursor.getInt(adminColumnIndex);
                    cursor.close();
                    mostrarMensajeDeInicioDeSesion(admin);
                    return admin;
                } else {
                    cursor.close();
                }
            } else {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Valor predeterminado si no se encuentra el usuario
    }

    protected static String encriptar(String contrasenia) throws Exception {
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


    private void mostrarMensajeDeInicioDeSesion(int admin) {
        if (admin == 1) {
            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso (administrador)", Toast.LENGTH_SHORT).show();
        } else if (admin == 0) {
            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }

    private long obtenerIdUsuario(String correo) {
        long idUsuario = -1;

        Cursor cursor = db.rawQuery("SELECT id FROM Usuarios WHERE Correo_e = ?",
                new String[]{correo});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                idUsuario = cursor.getLong(0);
            }
            cursor.close();
        }

        return idUsuario;
    }

    private void irAUsuarioActivity(long idUsuario, int admin) {
        guardarDatosUsuario(idUsuario, admin);

        Intent intent = new Intent(MainActivity.this, UsuarioActivity.class);
        startActivity(intent);
        finish();
    }

    private void irAAdminActivity(long idUsuario, int admin) {
        // Guardar información del usuario en preferencias compartidas con admin = 1 para administradores
        guardarDatosUsuario(idUsuario, admin);

        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    private void guardarDatosUsuario(long idUsuario, int admin) {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String nombreUsuario = obtenerNombreUsuario(idUsuario);
        String correoUsuario = obtenerCorreoUsuario(idUsuario);

        // Guardar el ID de usuario
        editor.putLong("idUsuario", idUsuario);
        editor.putInt("admin", admin);
        editor.putString("nombreUsuario", nombreUsuario);
        editor.putString("correoUsuario", correoUsuario);

        // Guardar cualquier otra información que desees recordar

        editor.apply();
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
}
