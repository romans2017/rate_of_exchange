package userProfiles;

import bankApi.BankEnum;
import bankApi.CurrencyEnum;

import java.util.List;
import java.util.Objects;

public class ProfileSettings {
    private int afterComma; //кол-во знаков после запятой
    private List<BankEnum> banks; //список банков
    private List<CurrencyEnum> currencies; // список валют
    private int hourNotification; //установленный час оповещения

    private ProfileSettings(int afterComma, List<BankEnum> banks, List<CurrencyEnum> currencies, int hourNotification) {
        this.afterComma = afterComma;
        this.banks = banks;
        this.currencies = currencies;
        this.hourNotification = hourNotification;
    }

    public ProfileSettings() {
        this(2, List.of(BankEnum.PRIVATBANK), List.of(CurrencyEnum.USD), 9);
    }

    public ProfileSettings setAfterComma(int afterComma) {
        this.afterComma = afterComma;
        return this;
    }

    public ProfileSettings setBanks(List<BankEnum> banks) {
        this.banks = banks;
        return this;
    }

    public ProfileSettings setCurrencies(List<CurrencyEnum> currencies) {
        this.currencies = currencies;
        return this;
    }

    public ProfileSettings setHourNotification(int hourNotification) {
        this.hourNotification = hourNotification;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileSettings)) return false;
        ProfileSettings that = (ProfileSettings) o;
        return afterComma == that.afterComma && hourNotification == that.hourNotification && banks.equals(that.banks) && currencies.equals(that.currencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(afterComma, banks, currencies, hourNotification);
    }
}