package com.example.primerproyecto;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ondas.db";


    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ResourceHelper.initialize(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       


        // Tabla Usuario
        String CREATE_USUARIO_TABLE = "CREATE TABLE Usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Nombre TEXT, " +
                "Apellidos TEXT, " +
                "Admin INTEGER, " +
                "Direccion TEXT, " +
                "Telefono INTEGER, " +
                "Correo_e TEXT, " +
                "Contrasenia TEXT, " +
                "Fecha_alta DATE, " +
                "NIF TEXT " +
                ")";

        db.execSQL(CREATE_USUARIO_TABLE);

        ContentValues valuesUsuarios = new ContentValues();


        valuesUsuarios.put("Nombre", "admin");
        valuesUsuarios.put("Apellidos", "admin");
        valuesUsuarios.put("Admin", 1);
        valuesUsuarios.put("Direccion", "greeneat 1");
        valuesUsuarios.put("Telefono", 954999999);
        valuesUsuarios.put("Correo_e", "admin@ondas.com");
        try {
            String contraseniaEncriptada = MainActivity.encriptar("admin");
            valuesUsuarios.put("Contrasenia", contraseniaEncriptada);
        } catch (Exception e) {
            e.printStackTrace();
        }        valuesUsuarios.put("NIF", "12345678A");
        valuesUsuarios.put("Fecha_alta", obtenerFechaActual());


        long result = db.insert("Usuarios", null, valuesUsuarios);

        if (result != -1) {
            Log.d("Database", "Usuario administrador creado exitosamente.");
            String contraseniaEncriptada = null;
            try {
                contraseniaEncriptada = MainActivity.encriptar("admin");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.d("Database", "Contraseña encriptada: " + contraseniaEncriptada);
        } else {
            Log.d("Database", "Error al crear el usuario administrador.");
        }

        

        // Tabla Lista
        String CREATE_Lista_TABLE = "CREATE TABLE Lista (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Nombre TEXT, " +
                "Categoria TEXT CHECK (Categoria IN ('ENTREVISTA', 'CONCIERTO', 'LISTA_ARTISTA', 'LISTA_TEMA', 'LISTA_ANIO')), " +
                "ENLACE TEXT, " +
                "Imagen TEXT "+
                ")";

        db.execSQL(CREATE_Lista_TABLE);


// Creación de un objeto ContentValues para la tabla Lista
        ContentValues valuesLista1 = new ContentValues();
        valuesLista1.put("Nombre", "Concierto de Verano");
        valuesLista1.put("Categoria", "CONCIERTO");
        valuesLista1.put("ENLACE", "http://ejemplo.com/concierto");
        valuesLista1.put("Imagen", "file:///android_asset/concierto_verano.png");

// Insertar los datos en la base de datos
        long idLista1 = db.insert("Lista", null, valuesLista1);

        ContentValues valuesLista2 = new ContentValues();
        valuesLista2.put("Nombre", "Entrevista con Arturo Vidal");
        valuesLista2.put("Categoria", "ENTREVISTA");
        valuesLista2.put("ENLACE", "http://ejemplo.com/entrevista_vidal");
        valuesLista2.put("Imagen", "file:///android_asset/vidal_entrevista.png");
        long idLista2 = db.insert("Lista", null, valuesLista2);

// Ejemplo 2: Insertar otra entrada de concierto
        ContentValues valuesLista3 = new ContentValues();
        valuesLista3.put("Nombre", "Festival Rock del Valle");
        valuesLista3.put("Categoria", "CONCIERTO");
        valuesLista3.put("ENLACE", "http://ejemplo.com/festival_rock");
        valuesLista3.put("Imagen", "file:///android_asset/festival_rock.png");
        long idLista3 = db.insert("Lista", null, valuesLista3);

// Ejemplo 3: Insertar una lista de temas
        ContentValues valuesLista4 = new ContentValues();
        valuesLista4.put("Nombre", "Top Hits 2023");
        valuesLista4.put("Categoria", "LISTA_TEMA");
        valuesLista4.put("ENLACE", "http://ejemplo.com/top_hits");
        valuesLista4.put("Imagen", "file:///android_asset/top_hits.png");
        long idLista4 = db.insert("Lista", null, valuesLista4);

// Ejemplo 4: Insertar una lista por artista
        ContentValues valuesLista5 = new ContentValues();
        valuesLista5.put("Nombre", "Discografía de Shakira");
        valuesLista5.put("Categoria", "LISTA_ARTISTA");
        valuesLista5.put("ENLACE", "http://ejemplo.com/shakira_discografia");
        valuesLista5.put("Imagen", "file:///android_asset/shakira.png");
        long idLista5 = db.insert("Lista", null, valuesLista5);

// Ejemplo 5: Insertar una lista por año
        ContentValues valuesLista6 = new ContentValues();
        valuesLista6.put("Nombre", "Los mejores del 1990");
        valuesLista6.put("Categoria", "LISTA_ANIO");
        valuesLista6.put("ENLACE", "http://ejemplo.com/mejores_1990");
        valuesLista6.put("Imagen", "file:///android_asset/mejores_1990.png");
        long idLista6 = db.insert("Lista", null, valuesLista6);


        String CREATE_Listas_Like_TABLE= "CREATE TABLE ListasLike ("  +
                "idLikes INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "idUsuario INTEGER,"+
                "idLista INTEGER,"+
                "FOREIGN KEY (idUsuario) REFERENCES Usuarios(id),"+
                "FOREIGN KEY (idLista) REFERENCES Listas(id)"+
                ")";
        db.execSQL(CREATE_Listas_Like_TABLE);



    


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE Proveedores");
        db.execSQL("DROP TABLE Usuarios");
        db.execSQL("DROP TABLE Listas");
        db.execSQL("DROP TABLE Pedidos");
        onCreate(db);
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = new Date();
        return dateFormat.format(fechaActual);
    }
    public boolean verificaExistenciaBaseDatos() {
        SQLiteDatabase db = getReadableDatabase();
        String path = db.getPath();
        db.close();
        return path != null && !path.isEmpty();
    }
    public void agregarListaDeseado(long idUsuario, long idLista) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idUsuario", idUsuario);
        values.put("idLista", idLista);

        db.insert("ListasDeseados", null, values);

        db.close();
    }
  
    public void eliminarListaDeseado(long idUsuario, long idLista) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "idUsuario = ? AND idLista = ?";
        String[] whereArgs = new String[]{String.valueOf(idUsuario), String.valueOf(idLista)};

        db.delete("ListasDeseados", whereClause, whereArgs);

        db.close();
    }
   
    



}

