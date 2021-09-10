import bankApi.BankEnum;
import facade.CashApiRequests;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import userProfiles.ProfileSettings;
import userProfiles.Profiles;

import java.io.BufferedReader;
import java.io.FileReader;

public class CurrencyTelegramBot extends TelegramLongPollingBot {

    private final String botName;
    private final String botToken;
    private final Profiles profiles;
    private final CashApiRequests cashApiRequests;

    public CurrencyTelegramBot() throws Exception {
        super();
        /*
        имя и токен бота должны хранится в текстовом файле \src\main\resources\botCredentials.ctxt
        в файле должна быть одна строка, в которой имя бота и токен разделены пробелом. Например:
        MyBot 12341241:gebsdfsbsdfbdsf
        Если файла не будет или файл не подойдет под указанные условия, то будет исключение, бот не запустится
         */
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/botCredentials.txt"))) {
            String[] botCredentials = bufferedReader.readLine().split(" ");
            botName = botCredentials[0];
            botToken = botCredentials[1];
        }

        //чтение (если есть откуда), создание дефолтных и запись в файл по расписанию профилей пользователей с настройками
        profiles = Profiles.getInstance();
        profiles.SchedulerSaveToFile();

        //запрос банков и запись ответов в мапу-кэш по расписанию
        cashApiRequests = CashApiRequests.getInstance();
        cashApiRequests.cashing();
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();

            //currentProfileSettings - объект, хранящий настройки текущего пользователя
            ProfileSettings currentProfileSettings = profiles.getProfileSettings(chatId);
            try {
                execute(SendMessage
                        .builder()
                        .chatId(chatId)
                        .text("It's alive!!!")
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        System.out.println("It's alive!!!");
    }

    public static void main(String[] args) throws TelegramApiException {
        CurrencyTelegramBot telegaBot;
        try {
            telegaBot = new CurrencyTelegramBot();
        } catch (Exception e) {
            System.out.println("Failed to initialize chat bot!!");
            e.printStackTrace();
            return;
        }
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegaBot);
    }
}
