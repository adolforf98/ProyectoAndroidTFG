package com.example.primerproyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class InicioFragment extends Fragment {
    private RecyclerView recyclerOfertas;
    private ListaAdapter productoOfertaAdapter;
    private boolean isImageChanged = false; // Variable para controlar el cambio de imagen

    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        ViewFlipper flipper = view.findViewById(R.id.flipper);
        flipper.startFlipping();

        // Obtener referencia al ImageView
        ImageView centeredImage = view.findViewById(R.id.centered_image);

        // Establecer OnClickListener para cambiar la imagen al hacer clic
        centeredImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageChanged) {
                    centeredImage.setImageResource(R.drawable.reproductor); // Imagen original
                } else {
                    centeredImage.setImageResource(R.drawable.reproductor2); // Imagen alternativa
                }
                isImageChanged = !isImageChanged; // Alternar estado
            }
        });

        return view;
    }
}
