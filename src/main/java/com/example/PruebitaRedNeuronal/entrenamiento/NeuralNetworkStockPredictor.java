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
        int epochs = 1000;
        int ordenCompra = 3;

        // Datos base
        double[] consumos = {
            3, 8, 5, 5, 3, 4, 8, 8, 6, 8,
            3, 7, 3, 3, 6, 6, 3, 3, 6, 5,
            8, 5, 8, 4, 6, 8, 6, 6, 3, 5,
            7, 8, 8, 8, 4, 5, 5, 6, 8, 7,
            3, 7, 4, 5, 8, 4, 8, 7, 6, 6,
            4, 7, 3, 6, 8, 6, 4, 7, 8, 3,
            7, 8, 8, 6, 3, 4, 7, 3, 5, 7,
            3, 8, 5, 8, 7, 6, 8, 7, 5, 8,
            5, 4, 6, 3, 5, 4, 3, 5, 4, 6,
            5, 6, 4, 5, 3, 4, 2, 3, 5, 4
        };

        double[] stocks = {
            200, 197, 189, 184, 229, 226, 222, 214, 256, 250,
            242, 239, 232, 229, 226, 270, 264, 261, 258, 252,
            247, 239, 234, 276, 272, 266, 258, 252, 296, 293,
            288, 281, 323, 315, 307, 303, 298, 293, 287, 329,
            322, 319, 312, 308, 303, 345, 341, 333, 326, 320,
            314, 310, 353, 350, 344, 336, 330, 376, 369, 361,
            358, 351, 343, 335, 329, 326, 322, 315, 312, 307,
            300, 297, 289, 284, 276, 269, 263, 255, 248, 243,
            235, 230, 226, 220, 217, 212, 208, 205, 200, 196,
            190, 185, 179, 175, 170, 167, 163, 161, 208, 203
        };

        double[] reposiciones = {4, 4, 7, 8, 5, 4, 7, 6, 7, 5, 5};

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

        File modelFile = new File("modelo_v4.zip");
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

