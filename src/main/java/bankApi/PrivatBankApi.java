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
            "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5"; //Наличный курс ПриватБанка (в отделениях)
    private static final URI uri =
            URI.create(GET_URL + new SimpleDateFormat("yyyyMMdd").format(new Date()));

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
            for (CurrencyEnum value : CurrencyEnum.values()) {
                if (currency.getCcy().equals(value.getValue())) {
                    result.add(currency);
                }
            }
        }

        return result;
    }

    private static class PrivatBankCurrency {
        String ccy;              //Код валюты
        String base_ccy;         //Код национальной валюты
        float buy;               //Курс покупки
        float sale;              //Курс продажи
        long date;

        public String getCcy() {
            return ccy;
        }

        public String getBase_ccy() {
            return base_ccy;
        }

        public float getBuy() {
            return buy;
        }

        public float getSale() {
            return sale;
        }

        public long getDate() {
            return date;
        }

        @Override
        public String toString() {
            return "PrivatBankCurrency{" +
                    "ccy='" + ccy + '\'' +
                    ", base_ccy='" + base_ccy + '\'' +
                    ", buy=" + buy +
                    ", sale=" + sale +
                    ", date=" + date +
                    '}';
        }
    }

}
