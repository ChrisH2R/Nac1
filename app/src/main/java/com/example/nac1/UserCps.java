package com.example.nac1;

public class UserCps {
    private double cps;

    // Constructor predeterminado necesario para la conversión de Firebase
    public UserCps() {
        // Necesario para la conversión de Firebase, incluso si está vacío
    }

    // Constructor con argumentos para facilitar la creación de instancias
    public UserCps(double cps) {
        this.cps = cps;
    }

    // Getter y setter para 'cps'
    public double getCps() {
        return cps;
    }

    public void setCps(double cps) {
        this.cps = cps;
    }
}
