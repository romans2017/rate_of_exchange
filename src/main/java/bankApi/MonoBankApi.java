package bankApi;

import bankApi.currency.MonoBankCurrency;
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

    private static final String CURRENCY_URL = "https://api.monobank.ua/bank/currency";

    public static List<MonoBankCurrency> getListOfCurrenciesRateMonoBank() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        final HttpRequest request = HttpRequest.newBuilder()
                                               .uri(URI.create(CURRENCY_URL))
                                               .GET()
                                               .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        List<MonoBankCurrency> currencies = gson.fromJson(response.body(), new TypeToken<List<MonoBankCurrency>>() {
        }.getType());


        List<MonoBankCurrency> result = new ArrayList<>();
        for (MonoBankCurrency monoBankCurrency : currencies) {
            for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                if (monoBankCurrency.getCurrencyCodeA() == currencyEnum.isoCode) {
                    result.add(monoBankCurrency);
                }
            }
        }
        return result;
    }
}

