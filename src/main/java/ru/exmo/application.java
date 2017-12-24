package ru.exmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import ru.exmo.process.tickerProcess;

/**
 * Created by Andrash on 23.12.2017.
 */
@ComponentScan
@EnableAutoConfiguration
public class application {
    public static void main(String[] args) {
        SpringApplication.run(application.class, args);
    }
}
