package ru.exmo.process;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.exmo.utils.HTTPClient;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Andrash on 23.12.2017.
 */
@EnableScheduling
@Component
public class tickerProcess {

    private List<String> currencyPair;

    @Autowired
    private HTTPClient httpClient;

    @PostConstruct
    public void init(){
        readSettings();
    }

    private void readSettings(){
        currencyPair = new ArrayList<>(Arrays.asList(
                "LTC_RUB","LTC_USD","LTC_EUR",
                    "ETH_RUB","ETH_USD","ETH_EUR",
                    "BTC_RUB","BTC_USD","BTC_EUR"));
    }

    @Scheduled(fixedRate = 2000)
    public void runProcess() {
        saveTickerToFile(returnTicker());
    }


    private void saveTickerToFile(String ticker) {
        Calendar calendar=Calendar.getInstance();

        StringBuilder fileName = new StringBuilder();
        fileName.append(calendar.get(calendar.YEAR)).append("_")
                .append(calendar.get(calendar.MONTH)).append("_")
                .append(calendar.get(calendar.DAY_OF_MONTH)).append("_")
                .append(calendar.get(calendar.HOUR_OF_DAY)).append("_")
                .append(calendar.get(calendar.MINUTE)).append("_")
                .append(calendar.get(calendar.SECOND))
                .append(".txt");

        try {
            PrintWriter out = new PrintWriter(fileName.toString() );
            try {
                out.print(ticker);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String returnTicker() {
        String resultJson = "";
        try {
            String URL = "https://api.exmo.com/v1/ticker/";
            resultJson = httpClient.getHttp(URL, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultJson;
    }
}
