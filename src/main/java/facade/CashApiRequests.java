package facade;

import bankApi.BankEnum;
import bankApi.NbuApi;
import bankApi.PrivatBankApi;

import java.io.IOException;
import java.util.*;
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
                List<PrivatBankApi.PrivatBankCurrency> responsePrivat = null;
                try {
//                    responseNBU = NbuApi.getListOfCurrenciesRate();
                } catch (Exception ignored) {
                }
                try {
                    responsePrivat = PrivatBankApi.getListOfCurrenciesRate();
                } catch (Exception ignored) {
                }

                locker.writeLock().lock();
                cashedData.put(BankEnum.NBU, responseNBU);
                cashedData.put(BankEnum.PRIVATBANK, responsePrivat);
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
