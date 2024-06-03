package com.example.primerproyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment {

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
                    stopRadioService();
                } else {
                    centeredImage.setImageResource(R.drawable.reproductor2); // Imagen alternativa
                    startRadioService("https://stream.zeno.fm/qlcihi4wkgztv");
                }
                isImageChanged = !isImageChanged; // Alternar estado
            }
        });

        return view;
    }

    private void startRadioService(String url) {
        Intent intent = new Intent(requireContext(), RadioService.class);
        intent.setAction("PLAY");
        intent.putExtra("URL", url);
        requireContext().startService(intent);
    }

    private void stopRadioService() {
        Intent intent = new Intent(requireContext(), RadioService.class);
        intent.setAction("STOP");
        requireContext().startService(intent);
    }
}
