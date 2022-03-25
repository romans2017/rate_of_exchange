package userProfiles;

import bankApi.BankEnum;
import bankApi.CurrencyEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProfileSettings implements Serializable {
    private int afterComma;
    private Set<BankEnum> banks;
    private Set<CurrencyEnum> currencies;
    private int hourNotification;

    public ProfileSettings() {
        banks = new HashSet<>();
        currencies = new HashSet<>();
        this.setAfterComma(2)
                .addBank(BankEnum.PRIVATBANK)
                .addCurrency(CurrencyEnum.USD)
                .setHourNotification(9);
    }

    /**
     * set number in decimal part into user profile
     */
    public synchronized ProfileSettings setAfterComma(int afterComma) {
        this.afterComma = afterComma;
        return this;
    }

    /**
     * set banks into user profile
     */
    public synchronized ProfileSettings setBanks(@NotNull Collection<BankEnum> banks) {
        this.banks = new HashSet<>(banks);
        return this;
    }

    /**
     * add bank into user profile
     */
    public synchronized ProfileSettings addBank(@NotNull BankEnum bank) {
        this.banks = new HashSet<>(banks); //на случай, если banks инициализировалось как List.of(...)
        this.banks.add(bank);
        return this;
    }

    /**
     * remove bank form user profile
     */
    public synchronized ProfileSettings removeBank(@NotNull BankEnum bank) {
        this.banks = new HashSet<>(banks); //на случай, если banks инициализировалось как List.of(...)
        this.banks.remove(bank);
        return this;
    }

    /**
     * set currencies into user profile
     */
    public synchronized ProfileSettings setCurrencies(@NotNull Collection<CurrencyEnum> currencies) {
        this.currencies = new HashSet<>(currencies);
        return this;
    }

    /**
     * add currency into user profile
     */
    public synchronized ProfileSettings addCurrency(@NotNull CurrencyEnum currency) {
        this.currencies = new HashSet<>(currencies);
        this.currencies.add(currency);
        return this;
    }

    /**
     * remove currency from user profile
     */
    public synchronized ProfileSettings removeCurrency(@NotNull CurrencyEnum currency) {
        this.currencies = new HashSet<>(currencies);
        this.currencies.remove(currency);
        return this;
    }

    /**
     * set notification hour into user profile
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