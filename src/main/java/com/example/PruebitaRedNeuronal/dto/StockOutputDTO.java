package com.example.PruebitaRedNeuronal.dto;

public class StockOutputDTO {
    private int diasEstimados;
    private String mensaje;

    public StockOutputDTO(int diasEstimados, String mensaje) {
        this.diasEstimados = diasEstimados;
        this.mensaje = mensaje;
    }

    public int getDiasEstimados() {
        return diasEstimados;
    }

    public void setDiasEstimados(int diasEstimados) {
        this.diasEstimados = diasEstimados;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}