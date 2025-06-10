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
    public StockOutputDTO predecir(@RequestBody StockInputDTO request) {
        return predictorService.predecir(request);
    }
}