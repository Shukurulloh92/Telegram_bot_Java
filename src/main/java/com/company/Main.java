package com.company;

import com.company.telegram.Bot;
import com.company.telegram.SQLServiseBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;

import static com.company.telegram.Bot.subjectsNameList;

public class Main {
    public static void main(String[] args) throws TelegramApiException {

        subjectsNameList.addAll(SQLServiseBot.getSubjects());

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new Bot());

        System.out.println("args="+Arrays.toString(args));
    }
}



