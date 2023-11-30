package com.example.nac1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebase {
    private DatabaseReference databaseReference;

    public MyFirebase() {
        // Obtén una referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveTopCps(double cps) {
        // Guarda el CPS en una ubicación específica en la base de datos
        // Aquí estoy usando un nodo llamado "top_cps"
        databaseReference.child("top_cps").push().setValue(cps);
    }
}
