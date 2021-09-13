package bankApi;

import com.google.gson.Gson;
import facade.CurrencyRate;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class PrivatBankApi {

    private static final String GET_URL =
            "https://api.privatbank.ua/p24api/exchange_rates?json&date="; //Архив курсов валют ПриватБанка
    private static final URI uri =
            URI.create(GET_URL + new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

    public static CurrencyRate getListOfCurrenciesRate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        CurrencyRate currencyRate = new CurrencyRate();
        List<String> listCurrencies = Arrays.stream(CurrencyEnum.values()).map(CurrencyEnum::getValue).collect(Collectors.toList());

        gson.fromJson(response.body(), PrivatBankCurrency.class)
                .getExchangeRate()
                .stream()
                .filter(item -> "UAH".equals(item.getBaseCurrency()) && item.getSaleRate() != 0.0f && listCurrencies.contains(item.getCurrency()))
                .forEach(item -> currencyRate.setRate(CurrencyEnum.valueOf(item.getCurrency()), new CurrencyRate.Rate(item.getSaleRate(), item.getPurchaseRate())));

        return currencyRate;
    }

    public static class PrivatBankCurrency {

        String date;
        String bank;
        int baseCurrency;
        String baseCurrencyLit;
        List<ExchangeRate> exchangeRate;

        static class ExchangeRate {
            String baseCurrency; //Базовая валюта (UAH)
            String currency; // Валюта сделки (USD, EUR, RUR, CHF, GBP, PLZ, SEK, XAU, CAD)
            float saleRateNB; //курс продажи НБУ
            float purchaseRateNB; //курс покупки НБУ
            float saleRate; //курс продажи Привата
            float purchaseRate; //курс покупки Привата

            public String getBaseCurrency() {
                return baseCurrency;
            }

            public String getCurrency() {
                return currency;
            }

            public float getSaleRateNB() {
                return saleRateNB;
            }

            public float getPurchaseRateNB() {
                return purchaseRateNB;
            }

            public float getSaleRate() {
                return saleRate;
            }

            public float getPurchaseRate() {
                return purchaseRate;
            }

            @Override
            public String toString() {
                return "ExchangeRate{" +
                        "baseCurrency='" + baseCurrency + '\'' +
                        ", currency='" + currency + '\'' +
                        ", saleRateNB=" + saleRateNB +
                        ", purchaseRateNB=" + purchaseRateNB +
                        ", saleRate=" + saleRate +
                        ", purchaseRate=" + purchaseRate +
                        '}';
            }
        }

        public String getDate() {
            return date;
        }

        public String getBank() {
            return bank;
        }

        public int getBaseCurrency() {
            return baseCurrency;
        }

        public String getBaseCurrencyLit() {
            return baseCurrencyLit;
        }

        public List<ExchangeRate> getExchangeRate() {
            return exchangeRate;
        }

        @Override
        public String toString() {
            return "PrivatBankCurrency{" +
                    "date='" + date + '\'' +
                    ", bank='" + bank + '\'' +
                    ", baseCurrency='" + baseCurrency + '\'' +
                    ", baseCurrencyLit='" + baseCurrencyLit + '\'' +
                    ", exchangeRate=" + exchangeRate +
                    '}';
        }
    }

}
