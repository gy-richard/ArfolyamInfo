package aej1yc.arfolyaminfo;

import java.util.Map;

public class CurrencyPair {
    private String fromCurrency;
    private String toCurrency;

    public CurrencyPair(String fromCurrency, String toCurrency) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public CurrencyPair(Map<String, Object> data) {
        this.fromCurrency = (String) data.get("fromCurrency");
        this.toCurrency = (String) data.get("toCurrency");
    }

    public String getFromCurrency() {
        return fromCurrency;
    }
    public String getToCurrency() {
        return toCurrency;
    }
}
