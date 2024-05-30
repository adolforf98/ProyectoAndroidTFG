package com.example.primerproyecto;

import java.io.Serializable;

public class Lista implements Serializable {
    private long id;
    private String nombre;
    private String categoria;
    private String enlace;
    private String imagen;

    // Constructor
    public Lista(long id, String nombre, String categoria, String enlace, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.enlace = enlace;
        this.imagen = imagen;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
