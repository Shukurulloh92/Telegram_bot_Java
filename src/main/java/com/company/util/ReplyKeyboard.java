package com.company.util;

import com.company.telegram.Bot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyKeyboard {

    public static List<KeyboardRow> subName() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        ArrayList<String> subjectNameList = new ArrayList<>(Bot.subjectsNameList);

        String icon = " ☕️️";
        int len = subjectNameList.size();

        if (len > 1){
                for (int i = 0; i < len; i+=2) {
                    if(7 <= i+1) {
                        KeyboardRow button = new KeyboardRow();
                        button.add(subjectNameList.get(i) + icon);
                        keyboardRows.add(button);
                    }else {
                    KeyboardRow button = new KeyboardRow();
                    button.add(subjectNameList.get(i) + icon);
                    button.add(subjectNameList.get(i+1) + icon);
                    keyboardRows.add(button);
                }
            }
        }
        keyboardRows.add(backButton());

        return keyboardRows;
    }


    public static KeyboardRow backButton() {

        KeyboardRow keyboardButtons = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Back ↩ ️️");
        keyboardButtons.add(keyboardButton);

        return keyboardButtons;
    }

    public static List<KeyboardRow> backList() {

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardButtons = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("Back ↩ ️️");
        keyboardButtons.add(keyboardButton);
        keyboardRows.add(keyboardButtons);

        return keyboardRows;
    }



    public static List<KeyboardRow> shareContact() {

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardButtons = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setRequestContact(true);
        keyboardButton.setText("Share contact");
        keyboardButtons.add(keyboardButton);
        keyboardRows.add(keyboardButtons);

        return keyboardRows;
    }

    public static List<KeyboardRow> menu() {

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardButtons1 = new KeyboardRow();
        KeyboardRow keyboardButtons2 = new KeyboardRow();
        KeyboardRow keyboardButtons3 = new KeyboardRow();

        KeyboardButton keyboardButton1 = new KeyboardButton();
        keyboardButton1.setText("Subjects \uD83D\uDCDA️️");

        KeyboardButton keyboardButton2 = new KeyboardButton();
        keyboardButton2.setText("My results \uD83D\uDCCB️️");

        KeyboardButton keyboardButton3 = new KeyboardButton();
        keyboardButton3.setText("My awards \uD83C\uDFC6️️");

        keyboardButtons1.add(keyboardButton1);
        keyboardButtons2.add(keyboardButton2);
        keyboardButtons3.add(keyboardButton3);

        keyboardRows.add(keyboardButtons1);
        keyboardRows.add(keyboardButtons2);
        keyboardRows.add(keyboardButtons3);

        return keyboardRows;
    }


}
