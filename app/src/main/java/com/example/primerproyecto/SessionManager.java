package com.example.primerproyecto;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class SessionManager {
    public static void cerrarSesion(AppCompatActivity activity) {
        // Borrar información del usuario en preferencias compartidas
        SharedPreferences prefs = activity.getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Borrar el ID de usuario y cualquier otra información relacionada
        editor.remove("idUsuario");
        editor.remove("admin");
        editor.apply();

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
