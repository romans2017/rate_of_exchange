package bankApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import facade.CurrencyRate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class NbuApi {

    private static final String GET_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json?date=";
    private static final URI uri = URI.create(GET_URL + new SimpleDateFormat("yyyyMMdd").format(new Date()));

    public static CurrencyRate getListOfCurrenciesRate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        List<NbuCurrency> currencies = gson.fromJson(response.body(), new TypeToken<List<NbuCurrency>>() {
        }.getType());

        CurrencyRate currencyRate = new CurrencyRate();
        for (NbuCurrency nbuCurrency : currencies) {
            for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                if (nbuCurrency.getCc().equals(currencyEnum.getValue())) {
                    currencyRate.setRate(currencyEnum, new CurrencyRate.Rate(nbuCurrency.getRate(), nbuCurrency.getRate()));
                }
            }
        }
        return currencyRate;
    }

    public static class NbuCurrency {
        int r030;
        String txt;
        float rate;
        String cc;
        String exchangedate;

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
