package com.example.nac1;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button clickButton;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private int clickCount = 0;
    private Button resetButton;
    private Button viewTopCpsButton;
    private long startTime;
    private long elapsedTime;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickButton = findViewById(R.id.clickButton);
        timerTextView = findViewById(R.id.timerTextView);
        resetButton = findViewById(R.id.resetButton);
        viewTopCpsButton = findViewById(R.id.viewTopCpsButton);

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

        viewTopCpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TopCpsActivity.class));
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timerTextView.setText("Crono: 10.00  Toques/s: " + String.format("%.2f", clickCount / 10.0));
                        clickButton.setText("Toques/s: " + String.format("%.2f", clickCount / 10.0));

                        // Envía los resultados a MQTT
                        sendResultsToMqtt(clickCount / 10.0);

                        // Guarda los 10 mejores CPS en Firebase
                        saveTopCps(clickCount / 10.0);
                    }
                });
            }
        }.start();

        clickButton.setText("Continua así!");
    }

    private void updateCps() {
        double cps = (double) clickCount / (elapsedTime / 1000.0);
        timerTextView.setText("Crono: " + String.format("%.2f", elapsedTime / 1000.0) +
                "    Toques/s: " + String.format("%.2f", cps));
    }

    private void resetTest() {
        clickCount = 0;
        elapsedTime = 0;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        clickButton.setEnabled(true);

        timerTextView.setText("Crono: 0.00  Toques/s: 0.00");
        clickButton.setText("Toca aquí para comenzar!");
    }
    private void sendResultsToMqtt(double cps) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String broker = "tcp://broker.emqx.io:1883";
                    String clientId = "AndroidSample112233";

                    MqttClient mqttClient = new MqttClient(broker, clientId, null);
                    MqttConnectOptions options = new MqttConnectOptions();
                    mqttClient.connect(options);

                    if (mqttClient.isConnected()) {
                        Log.d("MQTT", "Connected to broker");

                        String topic = "cps_topic";
                        String message = String.valueOf(cps);

                        // Crear una instancia de MqttMessage y establecer la calidad de servicio (QoS) en 2
                        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                        mqttMessage.setQos(2);

                        mqttClient.publish(topic, mqttMessage);

                        Log.d("MQTT", "Message published with QoS 2: " + message);

                        mqttClient.disconnect();
                        Log.d("MQTT", "Disconnected from broker");
                    } else {
                        Log.d("MQTT", "Failed to connect to broker");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void saveTopCps(double cps) {
        MyFirebase myFirebase = new MyFirebase();
        myFirebase.saveTopCps(cps);
    }
}