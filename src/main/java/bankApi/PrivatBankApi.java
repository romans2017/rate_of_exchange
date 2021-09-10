package bankApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrivatBankApi {

    private static final String GET_URL =
            "https://api.privatbank.ua/p24api/exchange_rates?json&date=01.12.2014"; //Архив курсов валют ПриватБанка
    private static final URI uri =
            URI.create(GET_URL + new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

    public static List<PrivatBankCurrency> getListOfCurrenciesRate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<PrivatBankCurrency> currencies = gson.fromJson(response.body(), new TypeToken<List<PrivatBankCurrency>>() {
        }.getType());

        List<PrivatBankCurrency> result = new ArrayList<>();
        for (PrivatBankCurrency currency : currencies) {
            for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                if (currency.getCurrency().equals(currencyEnum.getValue())) {
                    result.add(currency);
                }
            }
        }

        return result;
    }

    public static class PrivatBankCurrency {
        String currency;             //Валюта сделки
        String baseCurrency;         //Базовая валюта
        float saleRate;              //Курс продажи ПриватБанка
        float purchaseRate;          //Курс покупки ПриватБанка

        public String getCurrency() {
            return currency;
        }

        public String getBaseCurrency() {
            return baseCurrency;
        }

        public float getSaleRate() {
            return saleRate;
        }

        public float getPurchaseRate() {
            return purchaseRate;
        }

        @Override
        public String toString() {
            return "PrivatBankCurrency{" +
                    "currency='" + currency + '\'' +
                    ", baseCurrency='" + baseCurrency + '\'' +
                    ", saleRate=" + saleRate +
                    ", purchaseRate=" + purchaseRate +
                    '}';
        }
    }

}
