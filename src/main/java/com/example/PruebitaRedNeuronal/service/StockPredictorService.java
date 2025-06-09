package com.example.PruebitaRedNeuronal.service;

import java.io.File;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

@Service
public class StockPredictorService {
	 private MultiLayerNetwork model;

	    public StockPredictorService() throws Exception {
	        File modelFile = new File("C:\\Users\\suan_\\Downloads\\Integrador 2\\av3\\PruebitaRedNeuronal\\PruebitaRedNeuronal\\modelo.zip");
	        model = MultiLayerNetwork.load(modelFile, true);
	    }

	    public double predecirDiasHastaQuiebre(double consumoDiario, double stockActual, double promedioReposicion) {
	        INDArray entrada = Nd4j.create(new double[][]{{consumoDiario, stockActual, promedioReposicion}});
	        INDArray salida = model.output(entrada);
	        return salida.getDouble(0);
	    }
}
