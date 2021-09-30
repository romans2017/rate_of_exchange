package notifier;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import services.Shedule;
import userProfiles.Profiles;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotifTimer {
    Notifier notifier;

    public NotifTimer(TelegramLongPollingBot bot, Profiles profiles) {
        this.notifier = new Notifier(bot, profiles);
    }

    public void startNotifying() {
        TimeZone timeZoneUa = TimeZone.getTimeZone("Europe/Kiev");

        ScheduledExecutorService timer = Shedule.getInstance().getScheduledExecutorService();
        Runnable task = () -> {
            ZonedDateTime currentTime = ZonedDateTime.now(timeZoneUa.toZoneId());
            int hour = currentTime.getHour();
            if (hour >= 9 && hour <= 18) {
                notifier.sendNotifications(hour);
            }
        };

        ZonedDateTime currentTime = ZonedDateTime.now();
        ZonedDateTime nextHour = currentTime.withHour(Math.min(currentTime.getHour() + 1, 23)).withMinute(0).withSecond(0);
        timer.scheduleAtFixedRate(task, ChronoUnit.SECONDS.between(currentTime, nextHour), 3600L, TimeUnit.SECONDS);
    }
}
