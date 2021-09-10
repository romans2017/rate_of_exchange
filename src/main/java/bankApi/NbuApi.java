package bankApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NbuApi {
    private static final String GET_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json?date=";
    private static final URI uri = URI.create(GET_URL + new SimpleDateFormat("yyyyMMdd").format(new Date()));

    public static List<Currency> getListOfCurrenciesRate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        List<NbuCurrency> currencies = gson.fromJson(response.body(), new TypeToken<List<NbuCurrency>>() {
        }.getType());


        List<Currency> result = new ArrayList<>();
        for (NbuCurrency nbuCurrency : currencies) {
            for (CurrencyEnum currency : CurrencyEnum.values()) {
                if (currency.getValue().equals(nbuCurrency.getCc())) {
                    result.add(new Currency(currency, nbuCurrency.getRate(), nbuCurrency.getRate()));
                }
            }
        }

        return result;
    }

    public static class NbuCurrency {
        int r030;               //Цифровой ISO код валюты
        String txt;             //Название валюты на украинском
        float rate;             //Цена НБУ
        String cc;              //Буквенный ISO код валюты
        String exchangedate;    //Дата обмена

        public int getR030() {
            return r030;
        }

        public String getTxt() {
            return txt;
        }

        public float getRate() {
            return rate;
        }

        public String getCc() {
            return cc;
        }

        public String getExchangedate() {
            return exchangedate;
        }

        @Override
        public String toString() {
            return "NbuCurrency{" +
                    "r030=" + r030 +
                    ", txt='" + txt + '\'' +
                    ", rate=" + rate +
                    ", cc='" + cc + '\'' +
                    ", exchangedate=" + exchangedate +
                    '}';
        }
    }
}
