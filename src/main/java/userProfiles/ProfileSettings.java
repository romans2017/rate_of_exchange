package userProfiles;

import bankApi.BankEnum;
import bankApi.CurrencyEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileSettings implements Serializable {
    private int afterComma; //кол-во знаков после запятой
    private List<BankEnum> banks; //список банков
    private List<CurrencyEnum> currencies; // список валют
    private int hourNotification; //установленный час оповещения

    public ProfileSettings() {
        banks = new ArrayList<>();
        currencies = new ArrayList<>();
        this.setAfterComma(2)
                .addBank(BankEnum.PRIVATBANK)
                .addCurrency(CurrencyEnum.USD)
                .setHourNotification(9);
    }

    /**
     * устанавливает количество знаков после запятой в профиль пользователя
     */
    public ProfileSettings setAfterComma(int afterComma) {
        this.afterComma = afterComma;
        return this;
    }

    /**
     * устанавливает банки в профиль пользователя
     */
    public ProfileSettings setBanks(@NotNull List<BankEnum> banks) {
        this.banks = new ArrayList<>(banks);
        return this;
    }

    /**
     * добавляет банк в профиль пользователя
     */
    public ProfileSettings addBank(@NotNull BankEnum bank) {
        this.banks = new ArrayList<>(banks); //на случай, если banks инициализировалось как List.of(...)
        this.banks.add(bank);
        return this;
    }

    /**
     * удаляет банк из профиля пользователя
     */
    public ProfileSettings removeBank(@NotNull BankEnum bank) {
        this.banks = new ArrayList<>(banks); //на случай, если banks инициализировалось как List.of(...)
        this.banks.remove(bank);
        return this;
    }

    /**
     * устанавливает валюты в профиль пользователя
     */
    public ProfileSettings setCurrencies(@NotNull List<CurrencyEnum> currencies) {
        this.currencies = new ArrayList<>(currencies);
        return this;
    }

    /**
     * добавляет валюту в профиль пользователя
     */
    public ProfileSettings addCurrency(@NotNull CurrencyEnum currency) {
        this.currencies = new ArrayList<>(currencies); //на случай, если currencies инициализировалось как List.of(...)
        this.currencies.add(currency);
        return this;
    }

    /**
     * удаляет валюту из профиля пользователя
     */
    public ProfileSettings removeCurrency(@NotNull CurrencyEnum currency) {
        this.currencies = new ArrayList<>(currencies); //на случай, если currencies инициализировалось как List.of(...)
        this.currencies.remove(currency);
        return this;
    }

    /**
     * устанавливает час расписания в профиль пользователя
     */
    public ProfileSettings setHourNotification(int hourNotification) {
        this.hourNotification = hourNotification;
        return this;
    }

    public int getAfterComma() {
        return afterComma;
    }

    public List<BankEnum> getBanks() {
        return new ArrayList<>(banks);
    }

    public List<CurrencyEnum> getCurrencies() {
        return new ArrayList<>(currencies);
    }

    public int getHourNotification() {
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