package com.example.PruebitaRedNeuronal.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PruebitaRedNeuronal.dto.StockInputDTO;
import com.example.PruebitaRedNeuronal.dto.StockOutputDTO;
import com.example.PruebitaRedNeuronal.service.StockPredictorService;


@RestController
@RequestMapping("/api/stock")
public class RedNeuronalController {

	@Autowired
    private StockPredictorService predictorService;

    @PostMapping("/predecir")
    public StockOutputDTO predecir(@RequestBody StockInputDTO input) {
        double resultado = predictorService.predecirDiasHastaQuiebre(
                input.getConsumoDiario(),
                input.getStockActual(),
                input.getPromedioReposicion()
        );

        // Redondear el resultado a entero para comparar
        int diasEntero = (int) Math.round(resultado);
        String mensaje = "";

        switch (diasEntero) {
            case 3:
            case 2:
            case 1:
                mensaje = "⚠️ Alerta: el stock se agotará en " + diasEntero + " día(s). Realiza una orden urgente.";
                break;
            default:
                mensaje = "Stock dentro de parámetros normales.";
        }

        return new StockOutputDTO(resultado, mensaje);
    }
}
