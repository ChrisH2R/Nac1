package com.example.nac1;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopCpsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> topCpsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_cps);

        listView = findViewById(R.id.listView);
        Button btnBack = findViewById(R.id.btnBack);

        // Crear y configurar el adaptador
        topCpsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topCpsList);
        listView.setAdapter(adapter);

        // Obtén los 10 mejores CPS desde Firebase
        getTopCpsFromFirebase();

        // Configura el OnClickListener para el botón "Volver"
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Cierra la actividad actual
            }
        });
    }

    private void getTopCpsFromFirebase() {
        DatabaseReference topCpsRef = FirebaseDatabase.getInstance().getReference().child("top_cps");

        topCpsRef.orderByValue().limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topCpsList.clear();

                int position = 1;
                List<Double> cpsValues = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    double cpsValue = snapshot.getValue(Double.class);
                    cpsValues.add(cpsValue);
                }

                // Ordena la lista en orden descendente (mayor a menor)
                Collections.sort(cpsValues, Collections.reverseOrder());

                for (Double cpsValue : cpsValues) {
                    topCpsList.add(String.format("Top %d: %.2f cps", position++, cpsValue));
                }

                // Actualiza el adaptador
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja errores si es necesario
            }
        });
    }
}
