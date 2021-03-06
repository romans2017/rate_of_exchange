package facade;

import bankApi.BankEnum;
import bankApi.CurrencyEnum;
import services.Shedule;
import userProfiles.ProfileSettings;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CashApiRequests {

    private final Map<BankEnum, CurrencyRate> cashedData;
    private final ReadWriteLock locker;
    private static CashApiRequests instance;

    private CashApiRequests() {
        cashedData = new TreeMap<>();
        locker = new ReentrantReadWriteLock(true);
    }

    public static CashApiRequests getInstance() {
        if (instance == null) {
            instance = new CashApiRequests();
        }
        return instance;
    }

    public void cashing() {
        ScheduledExecutorService timer = Shedule.getInstance().getScheduledExecutorService();
        Runnable task = () -> Arrays.stream(BankEnum.values())
                .map(item -> {
                    try {
                        return Map.entry(item, (CurrencyRate) item.getMethod().invoke(null));
                    } catch (Exception ignored) {
                        return Map.entry(item, new CurrencyRate());
                    }
                })
                .forEach(item -> {
                    locker.writeLock().lock();
                    cashedData.put(item.getKey(), item.getValue());
                    locker.writeLock().unlock();
                });
        timer.scheduleAtFixedRate(task, 1L, 10L * 60L, TimeUnit.SECONDS);
    }

    public CurrencyRate getBankResponse(BankEnum bank) {

        locker.readLock().lock();
        CurrencyRate response = Optional
                .ofNullable(cashedData.get(bank))
                .orElse(new CurrencyRate());
        locker.readLock().unlock();
        return response;
    }

    public Map<BankEnum, CurrencyRate> getAllBankResponse() {

        locker.readLock().lock();
        Map<BankEnum, CurrencyRate> returnMap = new TreeMap<>(cashedData);
        locker.readLock().unlock();
        return returnMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        cashedData.forEach((key, value) -> sb
                .append(key.getValue())
                .append("\n")
                .append(value.toString())
                .append("\n"));
        return sb.toString();
    }

    public static String getNotificationForUser(ProfileSettings profileSettings) {

        CashApiRequests cashApiRequests = CashApiRequests.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        String patternRounding = "%." + profileSettings.getAfterComma() + "f";
        for (BankEnum bank : profileSettings.getBanks()) {
            CurrencyRate bankResponse = cashApiRequests.getBankResponse(bank);
            for (CurrencyEnum currency : profileSettings.getCurrencies()) {
                stringBuilder
                        .append("Rate in ")
                        .append(bank.getValue())
                        .append(": ")
                        .append(currency.getValue())
                        .append("/")
                        .append(CurrencyEnum.UAH.getValue())
                        .append("\n")
                        .append(" Buy: ")
                        .append(String.format(patternRounding, bankResponse.getRate(currency).getRateSale()))
                        .append("\n")
                        .append(" Sale: ")
                        .append(String.format(patternRounding, bankResponse.getRate(currency).getRatePurchase()))
                        .append("\n\n");
            }

        }
        return stringBuilder.toString();
    }
}
