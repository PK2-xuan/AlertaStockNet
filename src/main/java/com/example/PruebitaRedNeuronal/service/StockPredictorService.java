package com.example.PruebitaRedNeuronal.service;

import java.io.File;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;
import com.example.PruebitaRedNeuronal.dto.StockInputDTO;
import com.example.PruebitaRedNeuronal.dto.StockOutputDTO;

@Service
public class StockPredictorService {
	

    private final MultiLayerNetwork model;
    private final int ordenCompra = 3;

    public StockPredictorService() throws Exception {
        File modelFile = new File("C:\\Users\\suan_\\Downloads\\Integrador 2\\av3\\PruebitaRedNeuronal\\PruebitaRedNeuronal\\modelo.zip");
        this.model = MultiLayerNetwork.load(modelFile, true);
    }

    public StockOutputDTO predecirDiasHastaQuiebre(StockInputDTO input) {
        double consumoDiario = input.getConsumoDiario();
        double stockActual = input.getStockActual();

        // Entrada solo con 2 variables
        INDArray entrada = Nd4j.create(new double[][]{{consumoDiario, stockActual}});
        // dias traido desde la RN en decimal 
        //double diasEstimados = model.output(entrada).getDouble(0);
        // dias redondeado
        int diasEstimados = (int) model.output(entrada).getDouble(0); 
        int diasEstimadosEntero = (int) diasEstimados; 

        // Valores simulados desde histórico o configuración
        double[] consumosHistoricos = {
            3, 8, 5, 5, 3, 4, 8, 8, 6, 8, 3, 7, 3, 3, 6, 6, 3, 3, 6, 5
            // podrías cargar desde DB si quieres escalar
        };
        double[] reposicionesHistoricas = {
            4, 4, 7, 8, 5, 4, 7, 6, 7, 5, 5
        };

        double promedioVenta = calcularPromedio(consumosHistoricos);
        double promedioReposicion = calcularPromedio(reposicionesHistoricas);

        double stockMinimo = promedioVenta * (promedioReposicion + ordenCompra);
        double diasParaStockMinimo = (stockActual - stockMinimo) / consumoDiario;

        String mensaje;

        if (diasParaStockMinimo < 0) {
            mensaje = ""; // sin alerta
        } else if (diasParaStockMinimo == 0) {
            mensaje = "⚠️ ¡Hoy se llega al quiebre de stock!";
        } else if (diasParaStockMinimo <= 3) {
            mensaje = "⚠️ Alerta: el stock se agotará en " + (int)diasParaStockMinimo + " día(s). Realiza una orden urgente.";
        } else {
            mensaje = "✅ Stock dentro de parámetros normales.";
        }
        return new StockOutputDTO(diasEstimadosEntero, mensaje);
    }

    private double calcularPromedio(double[] valores) {
        double suma = 0;
        for (double val : valores) {
            suma += val;
        }
        return suma / valores.length;
    }
}