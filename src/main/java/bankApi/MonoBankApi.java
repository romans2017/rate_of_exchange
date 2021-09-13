package bankApi;

import bankApi.currency.MonoBankCurrency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import facade.CurrencyRate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class MonoBankApi {

    private static final String CURRENCY_URL = "https://api.monobank.ua/bank/currency";

    public static CurrencyRate getListOfCurrenciesRate()
            throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        final HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(CURRENCY_URL))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        List<MonoBankCurrency> currencies = gson.fromJson(response.body(), new TypeToken<List<MonoBankCurrency>>() {
        }.getType());

        CurrencyRate currencyRate = new CurrencyRate();
        for (MonoBankCurrency monoBankCurrency : currencies) {
            for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                if (monoBankCurrency.getCurrencyCodeA() == currencyEnum.isoCode
                        && monoBankCurrency.getCurrencyCodeB() == 980) {
                    currencyRate.setRate(currencyEnum, new CurrencyRate.Rate(monoBankCurrency.getRateSell(), monoBankCurrency.getRateBuy()));
                }
            }
        }
        return currencyRate;
    }
}

