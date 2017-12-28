package ru.exmo.process;

import javafx.beans.binding.StringBinding;
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
import javax.sql.DataSource;
import java.io.File;
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
    private final String URL_RETURN_TICKER = "https://api.exmo.com/v1/ticker/";

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

    @Scheduled(fixedRate = 3000)
    public void runProcess() {
        List<exmoTicker> tickers = returnTickers();
        for (exmoTicker ticker : tickers) {
            jdbcTemplate.update(
                    "INSERT INTO tikers(pair,high,low,avg,vol,vol_curr,last_trade,buy_price,sell_price) values(?,?,?,?,?,?,?,?,?)",
                    ticker.getPair(),
                    ticker.getHigh(),
                    ticker.getLow(),
                    ticker.getAvg(),
                    ticker.getVol(),
                    ticker.getVol_curr(),
                    ticker.getLast_trade(),
                    ticker.getBuy_price(),
                    ticker.getSell_price()
                    );
        }
    }


    private List<exmoTicker> returnTickers() {
        List<exmoTicker> listTicker = new ArrayList<>();
        try {
            String resultJson = httpClient.getHttp(URL_RETURN_TICKER, null);
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(resultJson);
            for (String pair : currencyPair) {
                Map<String, String> currentExmoPair = (Map<String, String>) jsonObject.get(pair);
                exmoTicker ticker = new exmoTicker();
                ticker.setPair(pair);
                ticker.setHigh(new BigDecimal(String.valueOf(currentExmoPair.get("high"))));
                ticker.setLow(new BigDecimal(String.valueOf(currentExmoPair.get("low"))));
                ticker.setAvg(new BigDecimal(String.valueOf(currentExmoPair.get("avg"))));
                ticker.setVol(new BigDecimal(String.valueOf(currentExmoPair.get("vol"))));
                ticker.setVol_curr(new BigDecimal(String.valueOf(currentExmoPair.get("vol_curr"))));
                ticker.setLast_trade(new BigDecimal(String.valueOf(currentExmoPair.get("last_trade"))));
                ticker.setBuy_price(new BigDecimal(String.valueOf(currentExmoPair.get("buy_price"))));
                ticker.setSell_price(new BigDecimal(String.valueOf(currentExmoPair.get("sell_price"))));
//                ticker.setUpdated(currentExmoPair.get("updated"));
                listTicker.add(ticker);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return listTicker;
    }

}
