package ru.exmo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.exmo.model.exmoTicker;
import ru.exmo.process.tickerProcess;

/**
 * Created by Andrash on 27.01.2018.
 */
@RestController
public class tickerController {

    @Autowired
    tickerProcess tickerProcess;

    @RequestMapping("/ticker")
    public exmoTicker getTicker(@RequestParam(value="pair") String name) {
        //todo не надежно
        exmoTicker ticker = tickerProcess.returnTicker(name);
        return ticker;
    }

}
