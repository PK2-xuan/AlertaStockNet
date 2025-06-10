package com.example.PruebitaRedNeuronal.dto;

public class StockOutputDTO {
    private double diasEstimados;
    private double riesgo;
    private String alerta;
    private String fechaQuiebre;
    
	public double getDiasEstimados() {
		return diasEstimados;
	}
	public void setDiasEstimados(double diasEstimados) {
		this.diasEstimados = diasEstimados;
	}
	public double getRiesgo() {
		return riesgo;
	}
	public void setRiesgo(double riesgo) {
		this.riesgo = riesgo;
	}
	public String getAlerta() {
		return alerta;
	}
	public void setAlerta(String alerta) {
		this.alerta = alerta;
	}
	public String getFechaQuiebre() {
		return fechaQuiebre;
	}
	public void setFechaQuiebre(String fechaQuiebre) {
		this.fechaQuiebre = fechaQuiebre;
	}


}