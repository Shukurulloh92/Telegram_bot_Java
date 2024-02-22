package com.company.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InlineKeyboar {
    public static List<List<InlineKeyboardButton>> testSolution() {

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("1⃣");
        button1.setCallbackData("1");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("2⃣");
        button2.setCallbackData("2");

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("3⃣");
        button3.setCallbackData("3");

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("4⃣");
        button4.setCallbackData("4");

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("\uD83D\uDE4F help");
        button5.setCallbackData("help me plz");
//        InlineKeyboardButton button = new InlineKeyboardButton();
//        button5.setText("\uD83D\uDED2 skip");
//        button5.setCallbackData("propustit");

        List<InlineKeyboardButton> buttonList1 = new ArrayList<>();
        buttonList1.add(button1);
        buttonList1.add(button2);
        buttonList1.add(button3);
        buttonList1.add(button4);

        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        buttonList2.add(button5);

        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(buttonList1);
        list.add(buttonList2);

        return list;
    }

    public static List<List<InlineKeyboardButton>> yesOrNo(boolean b) {

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Yes \uD83D\uDE00️️");
        button1.setCallbackData("yesss");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("No \uD83D\uDE41️️");
        button2.setCallbackData("noooo");

        List<InlineKeyboardButton> buttonList1 = new ArrayList<>();
        buttonList1.add(button2);
        if (b) {
            buttonList1.add(button1);
        }

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(buttonList1);

        return rowList;
    }


    public static List<List<InlineKeyboardButton>> userAwards(HashMap<Integer, String> certificateIdsList) {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (Map.Entry entry: certificateIdsList.entrySet()) {

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("\""+entry.getValue() + "\" certificate id " + entry.getKey());
            button.setCallbackData(entry.getKey().toString());

            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            buttonList.add(button);

            rowList.add(buttonList);
        }

        return rowList;
    }
}
