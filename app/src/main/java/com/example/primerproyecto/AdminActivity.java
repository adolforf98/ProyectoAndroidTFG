package com.example.primerproyecto;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {
    private final ListasFragment listasFragment = new ListasFragment();
    ClienteFragment clienteFragment = new ClienteFragment();

    DatosPersonalesFragment datosPersonalesFragment = new DatosPersonalesFragment();
    CrearAdminFragment crearAdminFragment = new CrearAdminFragment();
    private NavigationView lateralNavigationView;
    private NavigationBarView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        navigation = findViewById(R.id.bottom_navigationadmin);

        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(listasFragment);
        lateralNavigationView = findViewById(R.id.lateral_navigationadmin);
        lateralNavigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        showHideLateralNavigationView(false);

        View headerView = lateralNavigationView.getHeaderView(0);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        long idUsuario = prefs.getLong("idUsuario", -1);
        String nombreUsuario = prefs.getString("nombreUsuario", "");
        String correoUsuario = prefs.getString("correoUsuario", "");

        TextView nombreTextView = headerView.findViewById(R.id.nombreusuario);
        TextView correoTextView = headerView.findViewById(R.id.gmailusuario);

// Actualiza los textos con la información del usuario
        nombreTextView.setText(nombreUsuario);
        correoTextView.setText(correoUsuario);

    }
    private boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            // Otros casos aquí

            case "Cerrar sesion":
                // Manejar el clic en "Cerrar sesión"
                SessionManager.cerrarSesion(this);
                return true;
            case "Modificar datos":
                loadFragment(datosPersonalesFragment);
                showHideLateralNavigationView(false);
                return true;
            case "Crear admin":
                loadFragment(crearAdminFragment);
                showHideLateralNavigationView(false);
                return true;
        }
        return false;
    }

    private final  NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        switch (item.getTitle().toString()) {
            case "Productos":
                loadFragment(listasFragment);
                showHideLateralNavigationView(false);
                return true;

            case "Clientes":
                loadFragment(clienteFragment);
                showHideLateralNavigationView(false);
                return true;

            case "Cuenta":
                showHideLateralNavigationView(true);
                return true;
        }
        return false;
    };

    public void onBackPressed() {
        if (lateralNavigationView.getVisibility() == View.VISIBLE) {
            showHideLateralNavigationView(false);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_containeradmin);

            if (currentFragment == crearAdminFragment || currentFragment == datosPersonalesFragment) {
                loadFragment(listasFragment);
                navigation.setSelectedItemId(R.id.productosmenu);
            } else {
                super.onBackPressed();
            }
        }
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_containeradmin, fragment);
        transaction.commit();
    }
    private void showHideLateralNavigationView(boolean show) {
        if (show) {
            lateralNavigationView.setVisibility(View.VISIBLE);
            lateralNavigationView.setEnabled(true);
        } else {
            lateralNavigationView.setVisibility(View.GONE);
            lateralNavigationView.setEnabled(false);
        }
    }

}


