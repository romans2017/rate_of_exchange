package notifier;

import bankApi.BankEnum;
import bankApi.CurrencyEnum;
import facade.CashApiRequests;
import facade.CurrencyRate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import userProfiles.ProfileSettings;
import userProfiles.Profiles;

import java.util.concurrent.ConcurrentMap;

public class Notifier {

    private TelegramLongPollingBot bot;
    private CashApiRequests requests;
    private Profiles profiles;

    public Notifier(TelegramLongPollingBot bot, CashApiRequests requests, Profiles profiles) {
        this.bot = bot;
        this.requests = requests;
        this.profiles = profiles;
    }

    private void sendOneNotification(String chatId, ProfileSettings settings) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        StringBuilder outputSB = new StringBuilder();
        for (BankEnum bank : settings.getBanks()) {
            outputSB.append("Курс в " + bank.getValue() + ":\n");
            CurrencyRate rates = requests.getBankResponse(bank);
            for (CurrencyEnum curr : settings.getCurrencies()) {
                outputSB.append("  " + curr.getValue() + "/UAH:\n");
                CurrencyRate.Rate rate = rates.getRate(curr);
                double multiplier = Math.pow(10, settings.getAfterComma());
                double purchase_rate = Math.round(rate.getRatePurchase() * multiplier) / multiplier;
                double sale_rate = Math.round(rate.getRateSale() * multiplier) / multiplier;
                outputSB.append("    Покупка: " + purchase_rate + "\n    Продажа: " + sale_rate + "\n");
            }
        }
        String output = outputSB.toString().strip();
        sendMessage.setText(output);
        try {
            bot.execute(sendMessage);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendNotifications(NotificationEnum notifHour) {
        int hour = notifHour.ordinal() + 9;
        ConcurrentMap<String, ProfileSettings> settings = profiles.getChatsByHour(hour);
        for (String chatId: settings.keySet()) {
            sendOneNotification(chatId, settings.get(chatId));
        }
    }
}

