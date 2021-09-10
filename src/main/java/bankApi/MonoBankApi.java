package bankApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MonoBankApi {
    private static final String GET_URL = "https://api.monobank.ua/bank/currency";

    public static List<MonoBankApi.MonoBankCurrency> getListOfCurrenciesRate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        final HttpRequest request = HttpRequest.newBuilder()
                                               .uri(URI.create(GET_URL))
                                               .GET()
                                               .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        List<MonoBankApi.MonoBankCurrency> currencies = gson.
                fromJson(response.body(), new TypeToken<List<MonoBankApi.MonoBankCurrency>>() {
                }.getType());


        List<MonoBankApi.MonoBankCurrency> result = new ArrayList<>();
        for (MonoBankApi.MonoBankCurrency monoBankCurrency : currencies) {
            for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                result.add(monoBankCurrency);
            }
        }

        return result;
    }

    private static class MonoBankCurrency {

        int currencyCodeA;
        int currencyCodeB;
        int date;
        float rateSell;
        float rateBuy;
        float rateCross;

        public int getCurrencyCodeA() {
            return currencyCodeA;
        }

        public void setCurrencyCodeA(int currencyCodeA) {
            this.currencyCodeA = currencyCodeA;
        }

        public int getCurrencyCodeB() {
            return currencyCodeB;
        }

        public void setCurrencyCodeB(int currencyCodeB) {
            this.currencyCodeB = currencyCodeB;
        }

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public float getRateSell() {
            return rateSell;
        }

        public void setRateSell(float rateSell) {
            this.rateSell = rateSell;
        }

        public float getRateBuy() {
            return rateBuy;
        }

        public void setRateBuy(float rateBuy) {
            this.rateBuy = rateBuy;
        }

        public float getRateCross() {
            return rateCross;
        }

        public void setRateCross(float rateCross) {
            this.rateCross = rateCross;
        }

        @Override
        public String toString() {
            return "MonoBankCurrency{" +
                    "currencyCodeA=" + currencyCodeA +
                    ", currencyCodeB=" + currencyCodeB +
                    ", date=" + date +
                    ", rateSell=" + rateSell +
                    ", rateBuy=" + rateBuy +
                    ", rateCross=" + rateCross +
                    '}';
        }
    }
}
