package ru.exmo.process;

import javafx.beans.binding.StringBinding;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.exmo.model.exmoTicker;
import ru.exmo.utils.HTTPClient;

import javax.annotation.PostConstruct;
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
    private HTTPClient httpClient;

    @PostConstruct
    public void init() {
        readSettings();
        File dir = new File(getDirName(""));
        boolean created = dir.mkdir();
        if (!created)
            System.out.println("Каталог " + dir + " не создан");
        else
            System.out.println("Каталог " + dir + "успешно создан");

        for (String pair : currencyPair) {
            dir = new File(getDirName(pair));
            created = dir.mkdir();
            if (!created)
                System.out.println("Каталог " + dir + " не создан");
            else
                System.out.println("Каталог " + dir + " успешно создан");

        }

    }

    private void readSettings() {
        currencyPair = new ArrayList<>(Arrays.asList(
                "LTC_RUB", "LTC_USD", "LTC_EUR",
                "ETH_RUB", "ETH_USD", "ETH_EUR",
                "BTC_RUB", "BTC_USD", "BTC_EUR"));
    }

    @Scheduled(fixedRate = 2000)
    public void runProcess() {
        saveTickerToFile(returnTickers());
    }


    private void saveTickerToFile(List<exmoTicker> tickerList) {

        for (exmoTicker currentTicker : tickerList) {
            String fullFileName = getDirName(currentTicker.getPair()) + "\\" + getFileName();
            try {
                PrintWriter out = new PrintWriter(fullFileName);
                try {
                    out.print(currentTicker);
                } finally {
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    private String getDirName(String pair) {
        Calendar calendar = Calendar.getInstance();
        StringBuilder dirName = new StringBuilder();
        dirName.append(calendar.get(calendar.YEAR)).append("_")
                .append(calendar.get(calendar.MONTH)).append("_")
                .append(calendar.get(calendar.DAY_OF_MONTH));
        if (!"".equals(pair)) {
            dirName.append("\\").append(pair);
        }
        return dirName.toString();
    }

    private String getFileName() {
        Calendar calendar = Calendar.getInstance();
        StringBuilder fileName = new StringBuilder();
        fileName.append(calendar.get(calendar.YEAR)).append("_")
                .append(calendar.get(calendar.MONTH)).append("_")
                .append(calendar.get(calendar.DAY_OF_MONTH)).append("_")
                .append(calendar.get(calendar.HOUR_OF_DAY)).append("_")
                .append(calendar.get(calendar.MINUTE)).append("_")
                .append(calendar.get(calendar.SECOND))
                .append(".txt");
        return fileName.toString();
    }

}
