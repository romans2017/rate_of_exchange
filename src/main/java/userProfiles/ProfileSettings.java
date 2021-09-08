package userProfiles;

import bankApi.BankEnum;
import java.util.List;

public class ProfileSettings {
    private int afterComma;
    private List<BankEnum> banks;
    private int hourNotification;

    public ProfileSettings(int afterComma, List<BankEnum> banks, int hourNotification) {
        this.afterComma = afterComma;
        this.banks = banks;
        this.hourNotification = hourNotification;
    }

    public ProfileSettings() {
        this(2, List.of(BankEnum.PRIVATBANK), 9);
    }
}