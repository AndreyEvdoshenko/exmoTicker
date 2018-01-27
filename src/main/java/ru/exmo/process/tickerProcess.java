package ru.exmo.process;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.exmo.model.exmoTicker;
import ru.exmo.utils.HTTPClient;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Andrash on 23.12.2017.
 */
@EnableScheduling
@Component
public class tickerProcess {

    private List<String> currencyPair;
    private final String URL_RETURN_TICKER = "https://api.exmo.com/v1/ticker/";

    private volatile Map<String, exmoTicker> tickers = new ConcurrentHashMap<>();

    private static  int count = 0;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private HTTPClient httpClient;

    @PostConstruct
    public void init() {
        currencyPair = new ArrayList<>(Arrays.asList(
                "LTC_RUB", "LTC_USD", "LTC_EUR",
                "ETH_RUB", "ETH_USD", "ETH_EUR",
                "BTC_RUB", "BTC_USD", "BTC_EUR"));
    }

    @Scheduled(fixedRate = 5000)
    public void runProcess() {
        List<exmoTicker> exmoTickerList = returnTickers();
        System.out.println(count++ + " save ticker, interval - 5 sec");
        for (exmoTicker ticker : exmoTickerList) {
            tickers.put(ticker.getPair(),ticker);
            jdbcTemplate.update(
                    "INSERT INTO tikers(pair,high,low,avg,vol,vol_curr,last_trade,buy_price,sell_price,updated) values(?,?,?,?,?,?,?,?,?,?)",
                    ticker.getPair(),
                    ticker.getHigh(),
                    ticker.getLow(),
                    ticker.getAvg(),
                    ticker.getVol(),
                    ticker.getVol_curr(),
                    ticker.getLast_trade(),
                    ticker.getBuy_price(),
                    ticker.getSell_price(),
                    ticker.getUpdated()

            );
        }
    }

    @Scheduled(fixedRate = 12000000)
    public void deleteOldTicker(){
    //delete from tikers where update < timestamp
        count = 0;
        System.out.println("deleteOldTicker 20 min");
        this.jdbcTemplate.update(
                "delete from tikers where updated < ?",
                new Timestamp(System.currentTimeMillis()-12000000));
    }

    private List<exmoTicker> returnTickers() {
        List<exmoTicker> listTicker = new ArrayList<>();
        try {
            String resultJson = httpClient.getHttp(URL_RETURN_TICKER, null);
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(resultJson);
            Timestamp updatedTime = new Timestamp(System.currentTimeMillis());
            for (String pair : currencyPair) {
                Map<String, String> currentExmoPair = (Map<String, String>) jsonObject.get(pair);
                exmoTicker ticker = new exmoTicker();
                ticker.setPair(pair);
                ticker.setHigh(Float.parseFloat(currentExmoPair.get("high")));
                ticker.setLow(Float.parseFloat(String.valueOf(currentExmoPair.get("low"))));
                ticker.setAvg(Float.parseFloat(String.valueOf(currentExmoPair.get("avg"))));
                ticker.setVol(Float.parseFloat(String.valueOf(currentExmoPair.get("vol"))));
                ticker.setVol_curr(Float.parseFloat(String.valueOf(currentExmoPair.get("vol_curr"))));
                ticker.setLast_trade(Float.parseFloat(String.valueOf(currentExmoPair.get("last_trade"))));
                ticker.setBuy_price(Float.parseFloat(String.valueOf(currentExmoPair.get("buy_price"))));
                ticker.setSell_price(Float.parseFloat(String.valueOf(currentExmoPair.get("sell_price"))));
                ticker.setUpdated(updatedTime);
                listTicker.add(ticker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return listTicker;
    }

    public exmoTicker returnTicker(String pair){
        return tickers.get(pair);
    }

}
