package com.example.PruebitaRedNeuronal.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;
import com.example.PruebitaRedNeuronal.dto.StockInputDTO;
import com.example.PruebitaRedNeuronal.dto.StockOutputDTO;

@Service
public class StockPredictorService {
	
	 private final MultiLayerNetwork model;

	    public StockPredictorService() throws Exception {
	        model = MultiLayerNetwork.load(new File("modelo_v4_final.zip"), true);
	    }

	    public StockOutputDTO predecir(StockInputDTO request) {
	        INDArray input = Nd4j.create(new double[][]{{request.getConsumo(), request.getStock()}});
	        double diasEstimados = model.output(input).getDouble(0);

	        double promedioReposicion = 5.4;
	        double ordenCompra = 3;
	        double umbral = promedioReposicion + ordenCompra;
	        double riesgo = diasEstimados - umbral;

	        // Fecha estimada
	        LocalDate hoy = LocalDate.now();
	        LocalDate fechaQuiebre = hoy.plusDays((long) Math.ceil(diasEstimados));
	        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	        String alerta;
	        long diasFaltantes = Math.round(riesgo);
	        if (riesgo <= 0) {
	            alerta = "❌ ¡Alerta! El stock se romperá.";
	        } else if (diasFaltantes <= 3) {
	            alerta = "⚠️ En " + diasFaltantes + " días llegará al quiebre del stock mínimo.";
	        } else {
	            alerta = "✅ Stock saludable por ahora.";
	        }

	        StockOutputDTO response = new StockOutputDTO();
	        response.setDiasEstimados(Math.round(diasEstimados));
	        response.setRiesgo(Math.round(riesgo));
	        response.setAlerta(alerta);
	        response.setFechaQuiebre(fechaQuiebre.format(fmt));
	        return response;
	    }
}