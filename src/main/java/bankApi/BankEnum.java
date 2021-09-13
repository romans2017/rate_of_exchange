package bankApi;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

public enum BankEnum {
    PRIVATBANK("Приватбанк", PrivatBankApi.class),
    MONOBANK("Монобанк", MonoBankApi.class),
    NBU("НБУ", NbuApi.class);

    private final String value;
    private final Method method; //свойство для хранения метода, вызывающего по АПИ соответствующий банк

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
