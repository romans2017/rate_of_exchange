package facade;

import bankApi.BankEnum;
import bankApi.NbuApi;
import userProfiles.Profiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CashApiRequests {

    private final Map<BankEnum, Object> cashedData;
    private final ReadWriteLock locker;
    private static CashApiRequests instance;

    private CashApiRequests() {
        cashedData = new HashMap<>();
        locker = new ReentrantReadWriteLock(true);
    }

    /**
     * получает экземпляр Profiles из сериализованного файла или, если файла нет - создает новый
     */
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
                List<NbuApi.NbuCurrency> responseNBU = null;
                try {
                    responseNBU = NbuApi.getListOfCurrenciesRate();
                } catch (IOException | InterruptedException ignored) {
                }
                locker.writeLock().lock();
                cashedData.put(BankEnum.NBU, responseNBU);
                locker.writeLock().unlock();
            }
        }, 1000L, 5L * 60L * 1000L);
    }

    //получение ответа банка из кэша
    public Object getBankResponse(BankEnum bank) {

        locker.readLock().lock();
        Object responseNBU = cashedData.get(bank);
        locker.readLock().unlock();
        return responseNBU;
    }
}
