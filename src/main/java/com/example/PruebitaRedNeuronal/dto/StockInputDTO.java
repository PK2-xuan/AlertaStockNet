package com.example.PruebitaRedNeuronal.dto;

public class StockInputDTO {
   
	 private double consumoDiario;
	    private double stockActual;
	    private double promedioReposicion;

	    // Getters y Setters
	    public double getConsumoDiario() {
	        return consumoDiario;
	    }
	    public void setConsumoDiario(double consumoDiario) {
	        this.consumoDiario = consumoDiario;
	    }
	    public double getStockActual() {
	        return stockActual;
	    }
	    public void setStockActual(double stockActual) {
	        this.stockActual = stockActual;
	    }
	    public double getPromedioReposicion() {
	        return promedioReposicion;
	    }
	    public void setPromedioReposicion(double promedioReposicion) {
	        this.promedioReposicion = promedioReposicion;
	    }
    
}
