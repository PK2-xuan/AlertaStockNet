package com.example.PruebitaRedNeuronal.entrenamiento;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NeuralNetworkStockPredictor {

    public static void main(String[] args) throws Exception {
        int seed = 123;
        int batchSize = 10;
        int epochs = 1808;
        int ordenCompra = 3;

        // Datos base
        double[] consumos = {
        		6, 4, 3, 1, 2, 2, 2, 1, 1, 3,
        		5, 4, 5, 1, 1, 5, 2, 6, 4, 3,
        		2, 3, 2, 1, 3, 4, 4, 2, 5, 1,
        		1, 5, 2, 1, 1, 3, 3, 4, 2, 2,
        		2, 5, 3, 5, 2, 3, 2, 2, 6, 4,
        		6, 1, 6, 1, 2, 6, 4, 1, 2, 2,
        		6, 3, 6, 4, 3, 3, 3, 3, 5, 3,
        		4, 5, 1, 1, 5, 1, 3, 4, 5, 2,
        		1, 6, 3, 5, 4, 6, 2, 2, 2, 4,
        		1, 5, 2, 1, 3, 4, 5, 6, 6, 6,
        		6, 2, 1, 1, 4, 6, 6, 3, 3, 5,
        		3, 4, 3, 6, 2, 1, 4, 6, 6, 2,
        		2, 2, 1
        	    			};

        double[] stocks = {
        		400, 394, 390, 387, 386, 384, 382, 380, 379, 378,
        		375, 370, 366, 361, 360, 359, 354, 352, 346, 342,
        		339, 337, 334, 332, 331, 328, 324, 320, 318, 313,
        		312, 311, 306, 304, 303, 302, 299, 296, 292, 290,
        		288, 286, 281, 278, 273, 271, 268, 266, 264, 258,
        		254, 248, 247, 241, 240, 238, 232, 228, 227, 225,
        		223, 217, 214, 208, 204, 201, 198, 195, 192, 187,
        		184, 180, 175, 174, 173, 168, 167, 164, 160, 155,
        		153, 152, 146, 143, 138, 134, 128, 126, 124, 122,
        		118, 117, 112, 110, 109, 106, 102, 97, 91, 85,
        		79, 73, 71, 70, 69, 65, 59, 53, 50, 47,
        		42, 39, 35, 32, 26, 24, 23, 19, 13, 7,
        		5, 3, 1, 0
        				  };

        double[] reposiciones = {
        						 4, 5, 7, 8, 5, 4, 7, 6, 7, 4,
        						 5, 8, 4, 6, 4, 4, 5, 5, 4, 6
        						};

        // Entrenamiento: entrada = consumo + stock; salida = días estimados hasta fin de stock
        int dataSize = consumos.length;
        double[][] inputData = new double[dataSize][2];
        double[][] outputData = new double[dataSize][1];

        for (int i = 0; i < dataSize; i++) {
            inputData[i][0] = consumos[i];
            inputData[i][1] = stocks[i];
            outputData[i][0] = stocks[i] / consumos[i]; // Días hasta quiebre
        }

        INDArray inputNDArray = Nd4j.create(inputData);
        INDArray outputNDArray = Nd4j.create(outputData);
        DataSet dataset = new DataSet(inputNDArray, outputNDArray);
        List<DataSet> listDs = new ArrayList<>(dataset.asList());

        var iterator = new org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator<>(listDs, batchSize);

        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .updater(new Adam(0.01))
            .list()
            .layer(new DenseLayer.Builder().nIn(2).nOut(10).activation(Activation.RELU).build())
            .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY).nIn(10).nOut(1).build())
            .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        model.setListeners(new ScoreIterationListener(100));

        for (int i = 0; i < epochs; i++) {
            iterator.reset();
            model.fit(iterator);
        }

        File modelFile = new File("modelo_v4_final.zip");
        model.save(modelFile, true);
        System.out.println("✅ Modelo guardado en: " + modelFile.getAbsolutePath());

        // Datos de prueba
        double consumoTest = 6;
        double stockTest = 100;

        INDArray testInput = Nd4j.create(new double[][]{{consumoTest, stockTest}});
        double diasEstimados = model.output(testInput).getDouble(0);

        // Cálculos
        double promedioVenta = calcularPromedio(consumos);
        double promedioReposicion = calcularPromedio(reposiciones);
        double stockMinimo = promedioVenta * (promedioReposicion + ordenCompra);
        double diasParaStockMinimo = (stockTest - stockMinimo) / consumoTest;

        double umbralDias = promedioReposicion + ordenCompra;
        double riesgo = diasEstimados - umbralDias;

        // Fecha estimada
        LocalDate fechaInicio = LocalDate.of(2025, 06, 9);
        LocalDate fechaQuiebre = fechaInicio.plusDays((long) Math.ceil(diasEstimados));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Resultados
        System.out.println("\n📊 Resultados:");
        System.out.printf("Promedio venta diaria: %.2f%n", promedioVenta);
        System.out.printf("Promedio reposición: %.2f%n", promedioReposicion);
        System.out.println("Stock mínimo calculado: " + Math.round(stockMinimo));
        System.out.println("Días estimados hasta quiebre: " + Math.round(diasEstimados));
        System.out.println("📅 Fecha estimada de quiebre: " + fechaQuiebre.format(fmt));
        System.out.println("Días de margen (riesgo): " + Math.round(riesgo));

     /*   if (riesgo <= 0) {
            System.out.println("❌ ¡Alerta! El stock se romperá antes de poder reponer. Riesgo inminente.");
        } else if (riesgo <= 3) {
            System.out.println("⚠️ Riesgo cercano: considera una reposición inmediata.");
        } else {
            System.out.println("✅ Stock saludable por ahora. Tienes margen para operar.");
        }
        */
        
        long diasFaltantes = Math.round(riesgo);

        if (riesgo <= 0) {
            System.out.println("❌ ¡Alerta! El stock se romperá antes de poder reponer. Riesgo inminente.");
        } else if (diasFaltantes == 3) {
            System.out.println("⚠️ En 3 días llegará al quiebre del stock mínimo.");
        } else if (diasFaltantes == 2) {
            System.out.println("⚠️ En 2 días llegará al quiebre del stock mínimo.");
        } else if (diasFaltantes == 1) {
            System.out.println("⚠️ En 1 día llegará al quiebre del stock mínimo.");
        } else if (diasFaltantes > 3 && riesgo <= 5) {
            System.out.printf("⏳ El quiebre ocurrirá en aproximadamente %d días, estás a tiempo de reponer.%n", diasFaltantes);
        } else {
            System.out.println("✅ Stock saludable por ahora. Tienes buen margen para operar.");
        }

    }

    private static double calcularPromedio(double[] valores) {
        double suma = 0;
        for (double v : valores) suma += v;
        return suma / valores.length;
    }
    
}

