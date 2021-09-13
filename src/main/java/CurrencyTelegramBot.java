import bankApi.BankEnum;
import bankApi.CurrencyEnum;
import facade.CashApiRequests;
import notifier.NotificationEnum;
import notifier.Notifier;
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
import java.util.concurrent.TimeUnit;

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
//        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/botCredentials.ctxt"))) {
//            String[] botCredentials = bufferedReader.readLine().split(" ");
//            botName = botCredentials[0];
//            botToken = botCredentials[1];
//        }
        botName = "GoitProjectDMtestbot";
        botToken = "1907051961:AAGTB9NrTZym3LJjiZ2hsT_HK397sgf2380";

        //чтение (если есть откуда), создание дефолтных и запись в файл по расписанию профилей пользователей с настройками
        profiles = Profiles.getInstance();
        profiles.SchedulerSaveToFile();

        //запрос банков и запись ответов в мапу-кэш по расписанию
        cashApiRequests = CashApiRequests.getInstance();
        cashApiRequests.cashing();

        TimeUnit.SECONDS.sleep(4);
        List<BankEnum> banklist1 = List.of(BankEnum.NBU, BankEnum.MONOBANK);
        List<BankEnum> banklist2 = List.of(BankEnum.MONOBANK, BankEnum.NBU);
        List<BankEnum> banklist3 = List.of(BankEnum.MONOBANK);
        List<CurrencyEnum> currlist1 = List.of(CurrencyEnum.USD, CurrencyEnum.EUR);
        List<CurrencyEnum> currlist2 = List.of(CurrencyEnum.RUB);
        ProfileSettings settings1 = new ProfileSettings();
        ProfileSettings settings2 = new ProfileSettings();
        ProfileSettings settings3 = new ProfileSettings();
        settings1.setBanks(banklist1);
        settings2.setBanks(banklist2);
        settings3.setBanks(banklist3);
        settings1.setCurrencies(currlist1);
        settings2.setCurrencies(currlist1);
        settings3.setCurrencies(currlist2);
        settings1.setAfterComma(3);
        settings2.setAfterComma(2);
        settings3.setAfterComma(4);
        settings1.setHourNotification(10);
        settings2.setHourNotification(9);
        settings3.setHourNotification(10);
        profiles.setProfileSettings("769076398", settings1);
        profiles.setProfileSettings("1190950401", settings3);
        Notifier test = new Notifier(this, cashApiRequests, profiles);
        test.sendNotifications(NotificationEnum.Not10);
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
        //            ProfileSettings currentProfileSettings = profiles.getProfileSettings(update.getMessage().getChatId().toString());
        if (update.hasCallbackQuery()) {
            callBackQueryHandler(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            messageHandler(update.getMessage());
        }
    }


    private void messageHandler(Message message) {
        if (message.hasText()) {
            if (message.getText().equals("/start")) {
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
            }
        }
    }

    private void callBackQueryHandler(CallbackQuery callbackQuery) {

        switch (callbackQuery.getData()) {
            case "Settings":
                try {
                    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                    buttons.add(Collections.singletonList((InlineKeyboardButton.builder()
                            .text("Кол-во знаков после запятой")
                            .callbackData("Number")
                            .build())));
                    buttons.add(Collections.singletonList((InlineKeyboardButton.builder()
                            .text("Банк")
                            .callbackData("Bank_enum")
                            .build())));
                    buttons.add(Collections.singletonList((InlineKeyboardButton.builder()
                            .text("Валюты")
                            .callbackData("currencies")
                            .build())));
                    buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text("Время оповещений")
                            .callbackData("Time_of_notification")
                            .build()));

                    execute(
                            SendMessage.builder()
                                    .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                                    .text("Настройки")
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                    .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "Get":
                break;
            case "Number":
                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                        .text(getAfterComaButton(2, callbackQuery.getMessage().getChatId().toString()))
                        .callbackData("2")
                        .build()));
                buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                        .text(getAfterComaButton(3, callbackQuery.getMessage().getChatId().toString()))
                        .callbackData("3")
                        .build()));
                buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                        .text(getAfterComaButton(4, callbackQuery.getMessage().getChatId().toString()))
                        .callbackData("4")
                        .build()));
                try {
                    execute(
                            EditMessageReplyMarkup.builder()
                                    .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                                    .messageId(callbackQuery.getMessage().getMessageId())
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                    .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "2":
                profiles.getProfileSettings(callbackQuery.getMessage().getChatId().toString()).setAfterComma(2);
                break;
            case "3":
                profiles.getProfileSettings(callbackQuery.getMessage().getChatId().toString()).setAfterComma(3);
                break;
            case "4":
                profiles.getProfileSettings(callbackQuery.getMessage().getChatId().toString()).setAfterComma(4);
                break;
            case "Bank_enum":
                List<List<InlineKeyboardButton>> button = new ArrayList<>();
                for (BankEnum bankEnum : BankEnum.values()) {
                    button.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text(getBankEnumButton(bankEnum.name(), callbackQuery.getMessage().getChatId().toString()))
                            .callbackData(String.valueOf(bankEnum))
                            .build()));
                }
                try {
                    execute(EditMessageReplyMarkup.builder()
                            .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(button).build())
                            .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "currencies":
                List<List<InlineKeyboardButton>> button2 = new ArrayList<>();
                for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
                    button2.add(Arrays.asList(InlineKeyboardButton.builder()
                            .text(getCurrencyEnumButton(currencyEnum.name(), callbackQuery.getMessage().getChatId().toString()))
                            .callbackData(String.valueOf(currencyEnum))
                            .build()));
                }
                try {
                    execute(EditMessageReplyMarkup.builder()
                            .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(button2).build())
                            .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
//            case "Time_of_notification":
//                List<List<InlineKeyboardButton>> buttonNotif = new ArrayList<>();
//                List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
//                List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
//                List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
//                List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
//                keyboardButtonsRow1.add(KeyboardRow.
//                        .text(notifier.NotificationEnum.Not9.time)
//                        .callbackData("Notif:9")
//                        .build());
//                keyboardButtonsRow1.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not10.time)
//                        .callbackData("Notif:10")
//                        .build());
//                keyboardButtonsRow1.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not11.time)
//                        .callbackData("Notif:11")
//                        .build());
//                keyboardButtonsRow2.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not12.time)
//                        .callbackData("Notif:12")
//                        .build());
//                keyboardButtonsRow2.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not13.time)
//                        .callbackData("Notif:13")
//                        .build());
//                keyboardButtonsRow2.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not14.time)
//                        .callbackData("Notif:14")
//                        .build());
//                keyboardButtonsRow3.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not15.time)
//                        .callbackData("Notif:15")
//                        .build());
//                keyboardButtonsRow3.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not16.time)
//                        .callbackData("Notif:16")
//                        .build());
//                keyboardButtonsRow3.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not17.time)
//                        .callbackData("Notif:17")
//                        .build());
//                keyboardButtonsRow4.add(InlineKeyboardButton.builder()
//                        .text(notifier.NotificationEnum.Not18.time)
//                        .callbackData("Notif:18")
//                        .build());
//                keyboardButtonsRow4.add(InlineKeyboardButton.builder()
//                        .text("Выключить уведомления")
//                        .callbackData("Notif_of")
//                        .build());
//                buttonNotif.add(keyboardButtonsRow1);
//                buttonNotif.add(keyboardButtonsRow2);
//                buttonNotif.add(keyboardButtonsRow3);
//                buttonNotif.add(keyboardButtonsRow4);
//
//                try {
//                    execute(EditMessageReplyMarkup.builder()
//                            .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
//                            .messageId(callbackQuery.getMessage().getMessageId())
//                            .replyMarkup(ReplyKeyboardMarkup.builder().keyboard(buttonNotif).build())
//                            .build());
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }


        }
    }

    private String getAfterComaButton(int current, String chatId) {
        return profiles.getProfileSettings(chatId).getAfterComma() == current ? "✅ " + current : current + "";
    }

    private String getBankEnumButton(String current, String chatId) {
        String saved = String.valueOf(profiles.getProfileSettings(chatId).getBanks());
        return saved.equals("[" + current + "]") ? "✅ " + current : current + "";
    }

    private String getCurrencyEnumButton(String current, String chatId) {
        String saved = String.valueOf(profiles.getProfileSettings(chatId).getCurrencies());
        return saved.equals("[" + current + "]") ? "✅ " + current : current + "";
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
