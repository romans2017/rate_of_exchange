package userProfiles;

import bankApi.BankEnum;
import bankApi.CurrencyEnum;

import java.io.Serializable;
import java.util.List;

public class ProfileSettings implements Serializable {
    private int afterComma; //кол-во знаков после запятой
    private List<BankEnum> banks; //список банков
    private List<CurrencyEnum> currencies; // список валют
    private int hourNotification; //установленный час оповещения

    public ProfileSettings() {
        setAfterComma(2).setBanks(List.of(BankEnum.PRIVATBANK)).setCurrencies(List.of(CurrencyEnum.USD)).setHourNotification(9);
    }

    /**
     * устанавливает количество знаков после запятой в профиль пользователя
     * */
    public ProfileSettings setAfterComma(int afterComma) {
        this.afterComma = afterComma;
        return this;
    }

    /**
     * устанавливает банки в профиль пользователя
     * */
    public ProfileSettings setBanks(List<BankEnum> banks) {
        this.banks = banks;
        return this;
    }

    /**
     * устанавливает валюты в профиль пользователя
     * */
    public ProfileSettings setCurrencies(List<CurrencyEnum> currencies) {
        this.currencies = currencies;
        return this;
    }

    /**
     * устанавливает час расписания в профиль пользователя
     * */
    public ProfileSettings setHourNotification(int hourNotification) {
        this.hourNotification = hourNotification;
        return this;
    }

    public int getAfterComma() {
        return afterComma;
    }

    public List<BankEnum> getBanks() {
        return banks;
    }

    public List<CurrencyEnum> getCurrencies() {
        return currencies;
    }

    public int getHourNotification() {
        return hourNotification;
    }
}