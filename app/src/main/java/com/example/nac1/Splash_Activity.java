package com.example.nac1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class Splash_Activity extends AppCompatActivity {

    // Duración del splash screen en milisegundos
    private static final int SPLASH_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Utiliza un Handler para retrasar la apertura de MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Crea un Intent para abrir MainActivity
                Intent intent = new Intent(Splash_Activity.this, MainActivity.class);
                startActivity(intent);

                // Cierra Splash_Activity después de abrir MainActivity
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
