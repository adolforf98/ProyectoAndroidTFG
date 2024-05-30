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

public class UsuarioActivity extends AppCompatActivity {

    private final MostrarListaFragment listasFragment = new MostrarListaFragment();
    private final DatosPersonalesFragment datosPersonalesFragment = new DatosPersonalesFragment();
    private final LikesFragment likesFragment = new LikesFragment();
    private final InicioFragment inicioFragment = new InicioFragment();
    private NavigationView lateralNavigationView;
    private NavigationBarView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        lateralNavigationView = findViewById(R.id.lateral_navigation);
        lateralNavigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        showHideLateralNavigationView(false);

        View headerView = lateralNavigationView.getHeaderView(0);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String nombreUsuario = prefs.getString("nombreUsuario", "");
        String correoUsuario = prefs.getString("correoUsuario", "");

        TextView nombreTextView = headerView.findViewById(R.id.nombreusuario);
        TextView correoTextView = headerView.findViewById(R.id.gmailusuario);

        // Actualiza los textos con la información del usuario
        nombreTextView.setText(nombreUsuario);
        correoTextView.setText(correoUsuario);

        // Load the initial fragment
        loadFragment(inicioFragment);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Cerrar sesion":
                SessionManager.cerrarSesion(this);
                return true;
            case "Modificar datos":
                loadFragment(datosPersonalesFragment);
                showHideLateralNavigationView(false);
                return true;
            case "Lista de likes":
                loadFragment(likesFragment);
                showHideLateralNavigationView(false);
                return true;
            default:
                return false;
        }
    }

    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        switch (item.getTitle().toString()) {
            case "Inicio":
                loadFragment(inicioFragment);
                showHideLateralNavigationView(false);
                return true;
            case "Listas":
                loadFragment(listasFragment);
                showHideLateralNavigationView(false);
                return true;
            case "Cuenta":
                showHideLateralNavigationView(true);
                return true;
            default:
                return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (lateralNavigationView.getVisibility() == View.VISIBLE) {
            showHideLateralNavigationView(false);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

            if (currentFragment == datosPersonalesFragment || currentFragment == listasFragment) {
                loadFragment(inicioFragment);
                navigation.setSelectedItemId(R.id.iniciomenu);
                showHideLateralNavigationView(false);
            } else if (currentFragment == inicioFragment) {
                super.onBackPressed();
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void showHideLateralNavigationView(boolean show) {
        lateralNavigationView.setVisibility(show ? View.VISIBLE : View.GONE);
        lateralNavigationView.setEnabled(show);
    }
}
