package com.example.primerproyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class InvitadoActivity extends AppCompatActivity {
    InicioFragment inicioFragment = new InicioFragment();
    MostrarListaFragment listasFragment = new MostrarListaFragment();
    DatosPersonalesFragment datosPersonalesFragment = new DatosPersonalesFragment();
    MostrarListaFragment mostrarListaFragment = new MostrarListaFragment();
    private NavigationView lateralNavigationView;
    private NavigationBarView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitado);

        navigation = findViewById(R.id.bottom_navigationinvitado);

        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(inicioFragment);
        lateralNavigationView = findViewById(R.id.lateral_navigationinvitado);
        lateralNavigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        showHideLateralNavigationView(false);

    }
    private boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            // Otros casos aquí

            case "Regístrate":
                Intent intent = new Intent(InvitadoActivity.this, RegistroActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private final  NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        switch (item.getTitle().toString()) {
            case "Inicio":
                loadFragment(inicioFragment);
                showHideLateralNavigationView(false);
                return true;
            case "Carrito":
                loadFragment(listasFragment);
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
            // Si el lateral navigation está visible, ocúltalo
            showHideLateralNavigationView(false);
        } else {
            // Si el lateral navigation está oculto
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            if (currentFragment == datosPersonalesFragment) {
                // Si la cuenta está seleccionada, vuelve a inicio
                loadFragment(inicioFragment);
                navigation.setSelectedItemId(R.id.iniciomenu);
                showHideLateralNavigationView(false);
            } if (currentFragment == listasFragment) {
                loadFragment(inicioFragment);
                navigation.setSelectedItemId(R.id.iniciomenu);
            } else if (currentFragment == inicioFragment){
                super.onBackPressed();
            }
        }
    }



    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
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

