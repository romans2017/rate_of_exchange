package facade;

import bankApi.*;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CashApiRequests {

    private final Map<BankEnum, CurrencyRate> cashedData; //сортированная по ключу мапа мап - ключ основной мапы: банк; ключ вложенной (мапы из value) - валюта
    private final ReadWriteLock locker;
    private static CashApiRequests instance;

    private CashApiRequests() {
        cashedData = new TreeMap<>();
        locker = new ReentrantReadWriteLock(true);
    }

    //получает экземпляр Profiles из сериализованного файла или, если файла нет - создает новый
    public static CashApiRequests getInstance() {
        if (instance == null) {
            instance = new CashApiRequests();
        }
        return instance;
    }

    //запрос банков по расписанию
    public void cashing() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Arrays.stream(BankEnum.values())
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
            }
        }, 1000L, 5L * 60L * 1000L);
    }

    //получение ответа банка из кэша
    public CurrencyRate getBankResponse(BankEnum bank) {

        locker.readLock().lock();
        CurrencyRate response = cashedData.get(bank);
        locker.readLock().unlock();
        return response;
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
}
