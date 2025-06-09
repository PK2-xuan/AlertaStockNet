package com.example.PruebitaRedNeuronal.dto;

public class StockOutputDTO {
    private double diasEstimados;
    private String mensaje;

    public StockOutputDTO(double diasEstimados, String mensaje) {
        this.diasEstimados = diasEstimados;
        this.mensaje = mensaje;
    }

    public double getDiasEstimados() {
        return diasEstimados;
    }

    public void setDiasEstimados(double diasEstimados) {
        this.diasEstimados = diasEstimados;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
