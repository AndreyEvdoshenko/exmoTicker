package ru.exmo.model;

import org.json.simple.JSONObject;

import java.math.BigDecimal;

/**
 * Created by Andrash on 24.12.2017.
 */
public class exmoTicker {

    private String pair;
    private BigDecimal high; // максимальная цена сделки за 24 часа
    private BigDecimal low; //  минимальная цена сделки за 24 часа
    private BigDecimal avg; // средняя цена сделки за 24 часа
    private BigDecimal vol; //  объем всех сделок за 24 часа
    private BigDecimal vol_curr; //  сумма всех сделок за 24 часа
    private BigDecimal last_trade; //  цена последней сделки
    private BigDecimal buy_price; //  текущая максимальная цена покупки
    private BigDecimal sell_price; // текущая минимальная цена продажи
    private String updated; //  дата и время обновления данных

    public exmoTicker() {

    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public BigDecimal getVol_curr() {
        return vol_curr;
    }

    public void setVol_curr(BigDecimal vol_curr) {
        this.vol_curr = vol_curr;
    }

    public BigDecimal getLast_trade() {
        return last_trade;
    }

    public void setLast_trade(BigDecimal last_trade) {
        this.last_trade = last_trade;
    }

    public BigDecimal getBuy_price() {
        return buy_price;
    }

    public void setBuy_price(BigDecimal buy_price) {
        this.buy_price = buy_price;
    }

    public BigDecimal getSell_price() {
        return sell_price;
    }

    public void setSell_price(BigDecimal sell_price) {
        this.sell_price = sell_price;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public  String toString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sell_price",sell_price);
        jsonObject.put("buy_price",buy_price);
        jsonObject.put("last_trade",last_trade);
        jsonObject.put("vol_curr",vol_curr);
        jsonObject.put("vol",vol);
        jsonObject.put("avg",avg);
        jsonObject.put("low",low);
        jsonObject.put("high",high);
        jsonObject.put("pair",pair);
        return jsonObject.toJSONString();
    }

}
