package bankApi;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

public enum BankEnum {
    PRIVATBANK("Privatbank", PrivatBankApi.class),
    MONOBANK("Monobank", MonoBankApi.class),
    NBU("NBU", NbuApi.class);

    private final String value;
    private final Method method; //method which calls API of corresponding bank

    @SneakyThrows
    BankEnum(String value, Class<?> cl) {
        this.value = value;
        this.method = cl.getMethod("getListOfCurrenciesRate");
    }

    public String getValue() {
        return value;
    }

    public Method getMethod() {
        return method;
    }
}
