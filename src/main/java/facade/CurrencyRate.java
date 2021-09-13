package facade;

import bankApi.CurrencyEnum;

import java.util.Optional;
import java.util.TreeMap;

//класс нормализации ответа банка
public class CurrencyRate extends TreeMap<CurrencyEnum, CurrencyRate.Rate> {

    //класс структурі курса валюты
    public static class Rate {
        private float rateSale; //курс продажи
        private float ratePurchase; //курс покупки

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
        this.put(currency, rate);
    }

    public CurrencyRate.Rate getRate(CurrencyEnum currency) {
        return Optional.ofNullable(this.get(currency)).orElse(new Rate(0f, 0f));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.forEach((key, value) -> sb
                        .append(key.getValue())
                        .append(" - ")
                        .append("продажа: ")
                        .append(value.getRateSale())
                        .append(", покупка: ")
                        .append(value.getRateSale())
                        .append("\n"));
        return sb.toString();
    }
}
