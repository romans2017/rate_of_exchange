package bankApi;

public class Currency {
    private final CurrencyEnum currency;
    private final float saleRate;
    private final float purchaseRate;

    public Currency(CurrencyEnum currency, float saleRate, float purchaseRate) {
        this.currency = currency;
        this.saleRate = saleRate;
        this.purchaseRate = purchaseRate;
    }

    public CurrencyEnum getCurrency() {
        return currency;
    }

    public float getSaleRate() {
        return saleRate;
    }

    public float getPurchaseRate() {
        return purchaseRate;
    }
}
