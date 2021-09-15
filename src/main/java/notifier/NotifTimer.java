package notifier;

import bankApi.BankEnum;
import facade.CurrencyRate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import userProfiles.Profiles;

import java.util.*;

public class NotifTimer {
    Notifier notifier;

    public NotifTimer(TelegramLongPollingBot bot, Profiles profiles) {
        this.notifier = new Notifier(bot, profiles);
    }

    public void startNotifying() {
        Timer timer = new Timer(true);
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.MINUTE) > 0 || c.get(Calendar.SECOND) > 0) {
            c.add(Calendar.HOUR_OF_DAY, 1);
        }
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date startTime = c.getTime();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                if (hour >= 9 && hour <= 18){
                    notifier.sendNotifications(hour);
                }
            }
        }, startTime, 3600000L);
    }
}
