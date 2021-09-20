package services;

import bot.CurrencyTelegramBot;

public class Receiver implements Runnable {
    private CurrencyTelegramBot bot;

    public Receiver(CurrencyTelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {

    }
}
