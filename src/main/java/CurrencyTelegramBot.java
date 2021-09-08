import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class CurrencyTelegramBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "currensyChatBot";
    }

    @Override
    public String getBotToken() {
        return "1997525709:AAEb3Ei0W4taOMXgZt7-1P8-F5wUlQt2FrE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            try {
                execute(SendMessage
                        .builder()
                        .chatId(message.getChatId().toString())
                        .text("It's alive!!!")
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        System.out.println("It's alive!!!");
    }

    public static void main(String[] args) throws TelegramApiException {
        CurrencyTelegramBot telegaBot = new CurrencyTelegramBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegaBot);
    }
}
