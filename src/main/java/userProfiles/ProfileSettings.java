package userProfiles;

import bankApi.BankEnum;
import bankApi.CurrencyEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

public class ProfileSettings implements Serializable {
    private int afterComma; //кол-во знаков после запятой
    private Set<BankEnum> banks; //список банков
    private Set<CurrencyEnum> currencies; // список валют
    private int hourNotification; //установленный час оповещения

    public ProfileSettings() {
        banks = new HashSet<>();
        currencies = new HashSet<>();
        this.setAfterComma(2)
                .addBank(BankEnum.PRIVATBANK)
                .addCurrency(CurrencyEnum.USD)
                .setHourNotification(9);
    }

    /**
     * устанавливает количество знаков после запятой в профиль пользователя
     */
    public synchronized ProfileSettings setAfterComma(int afterComma) {
        this.afterComma = afterComma;
        return this;
    }

    /**
     * устанавливает банки в профиль пользователя
     */
    public synchronized ProfileSettings setBanks(@NotNull Collection<BankEnum> banks) {
        this.banks = new HashSet<>(banks);
        return this;
    }

    /**
     * добавляет банк в профиль пользователя
     */
    public synchronized ProfileSettings addBank(@NotNull BankEnum bank) {
        this.banks = new HashSet<>(banks); //на случай, если banks инициализировалось как List.of(...)
        this.banks.add(bank);
        return this;
    }

    /**
     * удаляет банк из профиля пользователя
     */
    public synchronized ProfileSettings removeBank(@NotNull BankEnum bank) {
        this.banks = new HashSet<>(banks); //на случай, если banks инициализировалось как List.of(...)
        this.banks.remove(bank);
        return this;
    }

    /**
     * устанавливает валюты в профиль пользователя
     */
    public synchronized ProfileSettings setCurrencies(@NotNull Collection<CurrencyEnum> currencies) {
        this.currencies = new HashSet<>(currencies);
        return this;
    }

    /**
     * добавляет валюту в профиль пользователя
     */
    public synchronized ProfileSettings addCurrency(@NotNull CurrencyEnum currency) {
        this.currencies = new HashSet<>(currencies); //на случай, если currencies инициализировалось как List.of(...)
        this.currencies.add(currency);
        return this;
    }

    /**
     * удаляет валюту из профиля пользователя
     */
    public synchronized ProfileSettings removeCurrency(@NotNull CurrencyEnum currency) {
        this.currencies = new HashSet<>(currencies); //на случай, если currencies инициализировалось как List.of(...)
        this.currencies.remove(currency);
        return this;
    }

    /**
     * устанавливает час расписания в профиль пользователя
     */
    public synchronized ProfileSettings setHourNotification(int hourNotification) {
        this.hourNotification = hourNotification;
        return this;
    }

    public synchronized int getAfterComma() {
        return afterComma;
    }

    public synchronized Set<BankEnum> getBanks() {
        return new HashSet<>(banks);
    }

    public synchronized Set<CurrencyEnum> getCurrencies() {
        return new HashSet<>(currencies);
    }

    public synchronized int getHourNotification() {
        return hourNotification;
    }

    @Override
    public String toString() {
        return "ProfileSettings{" +
            "afterComma=" + afterComma +
            ", banks=" + banks +
            ", currencies=" + currencies +
            ", hourNotification=" + hourNotification +
            '}'+hashCode();
    }
}