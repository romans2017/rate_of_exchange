import bankApi.BankEnum;
import bankApi.CurrencyEnum;
import facade.CashApiRequests;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import userProfiles.ProfileSettings;
import userProfiles.Profiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader("src/main/resources/botCredentials.ctxt"))) {
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
        //     currentProfileSettings - объект, хранящий настройки текущего пользователя
        /*if (update.hasMessage()) {
            ProfileSettings currentProfileSettings = profiles.getProfileSettings(update.getMessage().getChatId().toString());
            currentProfileSettings
                    .addBank(BankEnum.MONOBANK)
                    .addBank(BankEnum.NBU)
                    .addCurrency(CurrencyEnum.EUR);
        }*/
        if (update.hasCallbackQuery()) {
            callBackQueryHandler(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            messageHandler(update.getMessage());
        }
    }


    private void messageHandler(Message message) {
        if (message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    String chatId = message.getChatId().toString();
                    try {
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                                .callbackData("Get")
                                .text("Получить инфо")
                                .build()));
                        buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                                .text("Настройки")
                                .callbackData("Settings")
                                .build()));
                        execute(
                                SendMessage.builder()
                                        .text("Добро пожаловать. Этот бот поможет отслеживать актуальные курсы валют.")
                                        .chatId(chatId)
                                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                        .build());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                case "9:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(9);
                    break;
                case "10:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(10);
                    break;
                case "11:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(11);
                    break;
                case "12:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(12);
                    break;
                case "13:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(13);
                    break;
                case "14:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(14);
                    break;
                case "15:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(15);
                    break;
                case "16:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(16);
                    break;
                case "17:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(17);
                    break;
                case "18:00":
                    profiles.getProfileSettings(message.getChatId().toString()).setHourNotification(18);
                    break;

            }
        }
    }

    private void callBackQueryHandler(CallbackQuery callbackQuery) {

        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        String chatUserId = callbackQuery.getMessage().getChatId().toString();

        Integer messageUserId = callbackQuery.getMessage().getMessageId();


        switch (action) {
            case "Settings":
                try {
                    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                    buttons.add(Collections.singletonList((InlineKeyboardButton.builder()
                            .text("Кол-во знаков после запятой")
                            .callbackData("Number:" + "2")
                            .build())));
                    buttons.add(Collections.singletonList((InlineKeyboardButton.builder()
                            .text("Банк")
                            .callbackData("Bank_enum:" + "start_page")
                            .build())));
                    buttons.add(Collections.singletonList((InlineKeyboardButton.builder()
                            .text("Валюты")
                            .callbackData("currencies:" + "start_page")
                            .build())));
                    buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text("Время оповещений")
                            .callbackData("Time_of_notification")
                            .build()));
                    buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text("Назад")
                            .callbackData("Start")
                            .build()));

                    execute(
                            SendMessage.builder()
                                    .chatId(chatUserId)
                                    .text("Настройки")
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                    .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "Get":
                try {
                    List<List<InlineKeyboardButton>> buttons = List.of(
                            List.of(InlineKeyboardButton.builder()
                                    .text("Получить инфо")
                                    .callbackData("Get")
                                    .build()),
                            List.of(InlineKeyboardButton.builder()
                                    .text("Настройки")
                                    .callbackData("Settings")
                                    .build()));
                    execute(SendMessage.builder()
                            .text(CashApiRequests
                                    .getNotificationForUser(Profiles.getInstance().getProfileSettings(chatUserId)))
                            .chatId(chatUserId)
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                            .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "Number":
                String parametr = param[1];
                switch (parametr) {
                    case "2":
                        profiles.getProfileSettings(callbackQuery.getMessage().getChatId().toString())
                                .setAfterComma(2);
                        break;
                    case "3":

                        profiles.getProfileSettings(callbackQuery.getMessage().getChatId().toString())
                                .setAfterComma(3);
                        break;
                    case "4":
                        profiles.getProfileSettings(callbackQuery.getMessage().getChatId().toString())
                                .setAfterComma(4);
                        break;
                }

                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                for (int i = 2; i < 5; i++) {
                    buttons.add(List.of(InlineKeyboardButton
                            .builder()
                            .text(getAfterComaButton(i, chatUserId))
                            .callbackData("Number:" + i)
                            .build()));
                }
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text("Назад")
                        .callbackData("Settings")
                        .build()));
                try {
                    execute(
                            EditMessageReplyMarkup.builder()
                                    .chatId(chatUserId)
                                    .messageId(messageUserId)
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                    .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;

            case "Bank_enum":
                int size = profiles.getProfileSettings(chatUserId).getBanks().size();
                String parametrBank = param[1];
                switch (parametrBank) {
                    case "PRIVATBANK":
                        String saved = String.valueOf(profiles.getProfileSettings(chatUserId).getBanks());
                        String[] split = saved.split(",");
                        for (int i = 0; i < size; i++) {
                            if (split[i].contains("PRIVATBANK")) {
                                if (size != 1) {
                                    profiles.getProfileSettings(chatUserId).removeBank(BankEnum.PRIVATBANK);
                                    break;
                                }
                            } else {
                                profiles.getProfileSettings(chatUserId).addBank(BankEnum.PRIVATBANK);
                                if (i == size - 1) {
                                    break;
                                }
                            }
                        }
                        break;
                    case "MONOBANK":
                        String saved1 = String.valueOf(profiles.getProfileSettings(chatUserId).getBanks());
                        String[] split1 = saved1.split(",");
                        for (int i = 0; i < size; i++) {
                            if (split1[i].contains("MONOBANK")) {
                                if (size != 1) {
                                    profiles.getProfileSettings(chatUserId).removeBank(BankEnum.MONOBANK);
                                    break;
                                }
                            } else {
                                profiles.getProfileSettings(chatUserId).addBank(BankEnum.MONOBANK);
                                if (i == size - 1) {
                                    break;
                                }
                            }
                        }
                        break;
                    case "NBU":
                        String saved2 = String.valueOf(profiles.getProfileSettings(chatUserId).getBanks());
                        String[] split2 = saved2.split(",");
                        for (int i = 0; i < size; i++) {
                            if (split2[i].contains("NBU")) {
                                if (size != 1) {
                                    profiles.getProfileSettings(chatUserId).removeBank(BankEnum.NBU);
                                    break;
                                }
                            } else {
                                profiles.getProfileSettings(chatUserId).addBank(BankEnum.NBU);
                                if (i == size - 1) {
                                    break;
                                }
                            }
                        }
                        break;
                    case "start_page":
                        break;
                }
                List<List<InlineKeyboardButton>> button = new ArrayList<>();
                for (BankEnum bankEnum : BankEnum.values()) {
                    button.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text(getBankEnumButton(bankEnum.name(),
                                    chatUserId))
                            .callbackData("Bank_enum:" + String.valueOf(bankEnum))
                            .build()));
                }
                button.add(List.of(InlineKeyboardButton
                        .builder()
                        .text("Назад")
                        .callbackData("Settings")
                        .build()));
                try {
                    execute(EditMessageReplyMarkup.builder()
                            .chatId(chatUserId)
                            .messageId(messageUserId)
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(button).build())
                            .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;

            case "currencies":
                int sizeCurrencies = profiles.getProfileSettings(chatUserId).getCurrencies().size();
                String parametrCurrencies = param[1];

                switch (parametrCurrencies) {
                    case "USD":
                        String savedC1 = String.valueOf(profiles.getProfileSettings(chatUserId).getCurrencies());
                        String[] split = savedC1.split(",");
                        for (int i = 0; i < sizeCurrencies; i++) {
                            if (split[i].contains("USD")) {
                                if (sizeCurrencies != 1) {
                                    profiles.getProfileSettings(chatUserId).removeCurrency(CurrencyEnum.USD);
                                    break;
                                }
                            } else {
                                profiles.getProfileSettings(chatUserId).addCurrency(CurrencyEnum.USD);
                                if (i == sizeCurrencies - 1) {
                                    break;
                                }
                            }
                        }
                        break;
                    case "RUB":
                        String savedC2 = String.valueOf(profiles.getProfileSettings(chatUserId).getCurrencies());
                        String[] splitC2 = savedC2.split(",");
                        for (int i = 0; i < sizeCurrencies; i++) {
                            if (splitC2[i].contains("RUB")) {
                                if (sizeCurrencies != 1) {
                                    profiles.getProfileSettings(chatUserId).removeCurrency(CurrencyEnum.RUB);
                                    break;
                                }
                            } else {
                                profiles.getProfileSettings(chatUserId).addCurrency(CurrencyEnum.RUB);
                                if (i == sizeCurrencies - 1) {
                                    break;
                                }
                            }
                        }
                        break;
                    case "EUR":
                        String savedC3 = String.valueOf(profiles.getProfileSettings(chatUserId).getCurrencies());
                        String[] splitC3 = savedC3.split(",");
                        for (int i = 0; i < sizeCurrencies; i++) {

                            if (splitC3[i].contains("EUR")) {
                                if (sizeCurrencies != 1) {
                                    profiles.getProfileSettings(chatUserId).removeCurrency(CurrencyEnum.EUR);
                                    break;
                                }
                            } else {
                                profiles.getProfileSettings(chatUserId).addCurrency(CurrencyEnum.EUR);
                                if (i == sizeCurrencies - 1) {
                                    break;
                                }
                            }
                        }
                        break;
                    case "start_page":
                        break;
                }
                List<List<InlineKeyboardButton>> button2 = new ArrayList<>();
                for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                    if (!currencyEnum.equals(CurrencyEnum.UAH)) {
                        button2.add(Arrays.asList(InlineKeyboardButton.builder()
                                .text(getCurrencyEnumButton(currencyEnum.name(),
                                        callbackQuery.getMessage().getChatId().toString()))
                                .callbackData("currencies:" + String.valueOf(currencyEnum))
                                .build()));
                    }
                }
                button2.add(List.of(InlineKeyboardButton
                        .builder()
                        .text("Назад")
                        .callbackData("Settings")
                        .build()));
                try {
                    execute(EditMessageReplyMarkup.builder()
                            .chatId(chatUserId)
                            .messageId(messageUserId)
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(button2).build())
                            .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;

            case "Time_of_notification":
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setOneTimeKeyboard(true);

                List<KeyboardRow> keyboard = new ArrayList<>();

                int startHour = 9;
                int shiftHour = 0;
                for (int i = 0; i < 3; i++) {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    for (int j = startHour + shiftHour; j < startHour + shiftHour + 3; j++) {
                        keyboardRow.add(KeyboardButton.builder()
                                .text(j + ":00")
                                .build());
                    }
                    shiftHour += 3;
                    keyboard.add(keyboardRow);
                }

                KeyboardRow keyboardFourthRow = new KeyboardRow();
                keyboardFourthRow.add(KeyboardButton.builder()
                        .text("18:00")
                        .build());
                keyboardFourthRow.add(KeyboardButton.builder()
                        .text("Выключить уведомления")
                        .build());

                keyboard.add(keyboardFourthRow);
                replyKeyboardMarkup.setKeyboard(keyboard);

                try {
                    execute(
                            SendMessage.builder()
                                    .chatId(chatUserId)
                                    .text("Выберите время уведомлений")
                                    .replyMarkup(replyKeyboardMarkup)
                                    .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "Start":
                try {
                    List<List<InlineKeyboardButton>> buttons1 = new ArrayList<>();
                    buttons1.add(Arrays.asList(InlineKeyboardButton.builder()
                            .callbackData("Get")
                            .text("Получить инфо")
                            .build()));
                    buttons1.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text("Настройки")
                            .callbackData("Settings")
                            .build()));
                    execute(
                            SendMessage.builder()
                                    .text("Добро пожаловать. Этот бот поможет отслеживать актуальные курсы валют.")
                                    .chatId(chatUserId)
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons1).build())
                                    .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();

                }
        }
    }


    private String getAfterComaButton(int current, String chatId) {
        return profiles.getProfileSettings(chatId).getAfterComma() == current ? "✅ " + current
                : current + "";
    }

    private String getBankEnumButton(String current, String chatId) {
        int size = profiles.getProfileSettings(chatId).getBanks().size();
        String result = "";

        for (int i = 0; i < size; i++) {
            String saved = String.valueOf(profiles.getProfileSettings(chatId).getBanks());
            String[] split = saved.split(",");
            if (split[i].contains(current)) {
                result = "✅ " + current;
            } else {
                result = current;
            }
            if (result.contains("✅ ")) {
                return result;
            }
        }
        return result;
    }

    private String getCurrencyEnumButton(String current, String chatId) {
        int size = profiles.getProfileSettings(chatId).getCurrencies().size();
        String result = "";

        for (int i = 0; i < size; i++) {
            String saved = String.valueOf(profiles.getProfileSettings(chatId).getCurrencies());
            String[] split = saved.split(",");
            if (split[i].contains(current)) {
                result = "✅ " + current;
            } else {
                result = current;
            }
            if (result.contains("✅ ")) {
                return result;
            }
        }
        return result;
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
