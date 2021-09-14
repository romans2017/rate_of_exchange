import bankApi.BankEnum;
import bankApi.CurrencyEnum;
import facade.CashApiRequests;
import notifier.NotifTimer;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import userProfiles.ProfileSettings;
import userProfiles.Profiles;

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
    try (BufferedReader bufferedReader = new BufferedReader(
            new FileReader("src/main/botCredentials.ctxt"))) {
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

    NotifTimer timer = new NotifTimer(this, profiles);
    timer.startNotifying();

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

    String[] param = callbackQuery.getData().split(":");
    String action = param[0];

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
        try {
          String chatId = callbackQuery.getMessage().getChatId().toString();
          List<List<InlineKeyboardButton>> buttons = List.of(

                  List.of(InlineKeyboardButton.builder()
                          .text("Получить инфо")
                          .callbackData("Get")
                          .build()),
                  List.of(InlineKeyboardButton.builder()
                          .text("Настройки")
                          .callbackData("Settings")
                          .build())
          );

          execute(SendMessage.builder()
                  .text(CashApiRequests
                          .getNotificationForUser(Profiles.getInstance().getProfileSettings(chatId)))
                  .chatId(chatId)
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
        buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                .text(getAfterComaButton(2, callbackQuery.getMessage().getChatId().toString()))
                .callbackData("Number:" + "2")
                .build()));
        buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                .text(getAfterComaButton(3, callbackQuery.getMessage().getChatId().toString()))
                .callbackData("Number:" + "3")
                .build()));
        buttons.add(Arrays.asList(InlineKeyboardButton.builder()
                .text(getAfterComaButton(4, callbackQuery.getMessage().getChatId().toString()))
                .callbackData("Number:" + "4")
                .build()));

        try {
          execute(
                  EditMessageReplyMarkup.builder()
                          .chatId(callbackQuery.getMessage().getChatId().toString())
                          .messageId(callbackQuery.getMessage().getMessageId())
                          .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                          .build());
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
        break;

      case "Bank_enum":
        List<List<InlineKeyboardButton>> button = new ArrayList<>();
        for (BankEnum bankEnum : BankEnum.values()) {
          button.add(Arrays.asList(InlineKeyboardButton.builder()
                  .text(getBankEnumButton(bankEnum.name(),
                          callbackQuery.getMessage().getChatId().toString()))
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
          if (!currencyEnum.equals(CurrencyEnum.UAH)) {
            button2.add(Arrays.asList(InlineKeyboardButton.builder()
                    .text(getCurrencyEnumButton(currencyEnum.name(),
                            callbackQuery.getMessage().getChatId().toString()))
                    .callbackData(String.valueOf(currencyEnum))
                    .build()));
          }
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
        break;

      case "Time_of_notification":
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(KeyboardButton.builder()
                .text("9:00")
                .build());
        keyboardFirstRow.add(KeyboardButton.builder()
                .text("10:00")
                .build());
        keyboardFirstRow.add(KeyboardButton.builder()
                .text("11:00")
                .build());

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(KeyboardButton.builder()
                .text("12:00")
                .build());
        keyboardSecondRow.add(KeyboardButton.builder()
                .text("13:00")
                .build());
        keyboardSecondRow.add(KeyboardButton.builder()
                .text("14:00")
                .build());

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add(KeyboardButton.builder()
                .text("15:00")
                .build());
        keyboardThirdRow.add(KeyboardButton.builder()
                .text("16:00")
                .build());
        keyboardThirdRow.add(KeyboardButton.builder()
                .text("17:00")
                .build());

        KeyboardRow keyboardFourthRow = new KeyboardRow();
        keyboardFourthRow.add(KeyboardButton.builder()
                .text("18:00")
                .build());
        keyboardFourthRow.add(KeyboardButton.builder()
                .text("Выключить уведомления")
                .build());

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        keyboard.add(keyboardFourthRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        try {
          execute(
                  SendMessage.builder()
                          .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                          .text("Выберите время уведомлений")
                          .replyMarkup(replyKeyboardMarkup)
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