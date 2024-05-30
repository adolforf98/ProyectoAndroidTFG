package com.example.primerproyecto;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ResourceHelper {

    private static Context appContext; // Initialize this when your application starts

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Resources getResources() {
        if (appContext == null) {
            throw new IllegalStateException("ResourceHelper not initialized. Call initialize() first.");
        }
        return appContext.getResources();
    }

    public static Bitmap getBitmapFromDrawable(int drawableId) {
        return BitmapFactory.decodeResource(getResources(), drawableId);
    }
}

