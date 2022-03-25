package facade;

import bankApi.CurrencyEnum;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class CurrencyRate {

    private final Map<CurrencyEnum, Rate> mapCurrency;

    public CurrencyRate() {
        this.mapCurrency = new TreeMap<>();
    }

    public static class Rate {
        private float rateSale;
        private float ratePurchase;

        public Rate(float rateSale, float ratePurchase) {
            this.rateSale = rateSale;
            this.ratePurchase = ratePurchase;
        }

        public float getRateSale() {
            return rateSale;
        }

        public void setRateSale(float rateSale) {
            this.rateSale = rateSale;
        }

        public float getRatePurchase() {
            return ratePurchase;
        }

        public void setRatePurchase(float ratePurchase) {
            this.ratePurchase = ratePurchase;
        }
    }

    public void setRate(CurrencyEnum currency, CurrencyRate.Rate rate) {
        mapCurrency.put(currency, rate);
    }

    public CurrencyRate.Rate getRate(CurrencyEnum currency) {
        return Optional
                .ofNullable(mapCurrency.get(currency))
                .orElse(new Rate(0f, 0f));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        mapCurrency.forEach((key, value) -> sb
                        .append(key.getValue())
                        .append(" - ")
                        .append("buy: ")
                        .append(value.getRateSale())
                        .append(", sale: ")
                        .append(value.getRatePurchase())
                        .append("\n"));
        return sb.toString();
    }
}
