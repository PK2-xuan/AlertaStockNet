package com.example.PruebitaRedNeuronal.entrenamiento;

import java.io.File;
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
        // Parámetros
        int seed = 123;
        int batchSize = 10;
        int epochs = 1000;

        // Datos simulados: [consumoDiario, stockActual, promedioReposicion]
        double[][] inputData = new double[][] {
            {10, 100, 5},
            {12, 80, 6},
            {9, 90, 4},
            {11, 60, 7},
            {8, 100, 6},
            {7, 40, 5},
            {10, 50, 4},
            {13, 70, 6},
            {9, 90, 7},
            {12, 60, 5}
        };

        // Salidas esperadas: días hasta quiebre (stock / consumo)
        double[][] outputData = new double[inputData.length][1];
        for (int i = 0; i < inputData.length; i++) {
            double consumo = inputData[i][0];
            double stock = inputData[i][1];
            outputData[i][0] = stock / consumo;
        }

        INDArray inputNDArray = Nd4j.create(inputData);
        INDArray outputNDArray = Nd4j.create(outputData);
        DataSet dataset = new DataSet(inputNDArray, outputNDArray);
        List<DataSet> listDs = dataset.asList();
        org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator<DataSet> iterator =
            new org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator<>(listDs, batchSize);

        // Configuración de red neuronal
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .updater(new Adam(0.01))
            .list()
            .layer(new DenseLayer.Builder().nIn(3).nOut(5)
                .activation(Activation.RELU)
                .build())
            .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY)
                .nIn(5).nOut(1)
                .build())
            .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();
        model.setListeners(new ScoreIterationListener(100));

        // Entrenamiento
        for (int i = 0; i < epochs; i++) {
            iterator.reset();
            model.fit(iterator);
        }

        // Guardar modelo
        File modelFile = new File("C:\\Users\\suan_\\Downloads\\Integrador 2\\av3\\PruebitaRedNeuronal\\PruebitaRedNeuronal\\modelo.zip");
        model.save(modelFile, true);
        System.out.println("✅ Modelo guardado en: " + modelFile.getAbsolutePath());

        // Prueba de predicción con matriz 2D (forma correcta)
        INDArray ejemplo = Nd4j.create(new double[][]{{10, 70, 6}});
        INDArray resultado = model.output(ejemplo);
        System.out.println("🔍 Días hasta quiebre estimados: " + resultado.getDouble(0));
    }
}

