package bankApi.currency;

public class MonoBankCurrency {

    private int currencyCodeA;
    private int currencyCodeB;
    private long date;
    private float rateSell;
    private float rateBuy;
    private float rateCross;

    public int getCurrencyCodeA() {
        return currencyCodeA;
    }

    public int getCurrencyCodeB() {
        return currencyCodeB;
    }

    public long getDate() {
        return date;
    }

    public float getRateSell() {
        return rateSell;
    }

    public float getRateBuy() {
        return rateBuy;
    }

    public float getRateCross() {
        return rateCross;
    }

    public void setCurrencyCodeA(int currencyCodeA) {
        this.currencyCodeA = currencyCodeA;
    }

    public void setCurrencyCodeB(int currencyCodeB) {
        this.currencyCodeB = currencyCodeB;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setRateSell(float rateSell) {
        this.rateSell = rateSell;
    }

    public void setRateBuy(float rateBuy) {
        this.rateBuy = rateBuy;
    }

    public void setRateCross(float rateCross) {
        this.rateCross = rateCross;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonoBankCurrency that = (MonoBankCurrency) o;

        if (currencyCodeA != that.currencyCodeA) return false;
        return currencyCodeB == that.currencyCodeB;
    }

    @Override
    public int hashCode() {
        int result = currencyCodeA;
        result = 31 * result + currencyCodeB;
        return result;
    }

    @Override
    public String toString() {
        return "MonoBankCurrency{" +
                "currencyCodeA=" + currencyCodeA +
                ", currencyCodeB=" + currencyCodeB +
                ", date=" + date +
                ", rateSell=" + rateSell +
                ", rateBuy=" + rateBuy +
                ", rateCross=" + rateCross +
                '}';
    }
}
