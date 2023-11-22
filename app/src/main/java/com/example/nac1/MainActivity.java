package com.example.nac1;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button clickButton;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private int clickCount = 0;
    private Button resetButton;

    private long startTime;
    private long elapsedTime;

    // Handler y Runnable para actualizar el texto cada milisegundo
    private Handler handler = new Handler();
    private Runnable updateTextRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickButton = findViewById(R.id.clickButton);
        timerTextView = findViewById(R.id.timerTextView);
        resetButton = findViewById(R.id.resetButton);

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer == null) {
                    startCountdown();
                }

                clickCount++;
                updateCps();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTest();
            }
        });
    }

    private void startCountdown() {
        startTime = SystemClock.elapsedRealtime();

        countDownTimer = new CountDownTimer(10000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime;
                updateCps();
            }

            @Override
            public void onFinish() {
                clickButton.setEnabled(false);
                // Cuando el temporizador se completa, establece el cronómetro en 10.00
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timerTextView.setText("Crono: 10.00  Toques/s: " + String.format("%.2f", clickCount / 10.0));
                        // Cambia el texto del botón al finalizar el temporizador
                        clickButton.setText("Toques/s: " + String.format("%.2f", clickCount / 10.0));
                    }
                });
            }
        }.start();

        // Cambia el texto del botón al comenzar a dar toques
        clickButton.setText("Continua asi!");
    }

    private void updateCps() {
        // Calcula y muestra los CPS en tiempo real
        double cps = (double) clickCount / (elapsedTime / 1000.0);
        timerTextView.setText("Crono: " + String.format("%.2f", elapsedTime / 1000.0) +
                "    Toques/s: " + String.format("%.2f", cps));
    }

    private void resetTest() {
        // Reinicia los valores relevantes
        clickCount = 0;
        elapsedTime = 0;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        // Habilita el botón de clic
        clickButton.setEnabled(true);

        // Restablece los textos a los valores iniciales
        timerTextView.setText("Crono: 0.00  Toques/s: 0.00");
        clickButton.setText("Toca aqui para comenzar!");
    }
}
