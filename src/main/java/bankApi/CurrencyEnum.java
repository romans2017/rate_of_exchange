package bankApi;

public enum CurrencyEnum {
    UAH("UAH", 980),
    USD("USD", 840),
    EUR("EUR", 978),
    RUB("RUB", 643);

    String value;
    int isoCode;
    CurrencyEnum(String value, int isoCode) {
        this.value = value;
        this.isoCode = isoCode;
    }

    public String getValue() {
        return value;
    }

    public int getIsoCode() {
        return isoCode;
    }
}
