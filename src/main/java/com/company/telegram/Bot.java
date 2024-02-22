package com.company.telegram;

import com.company.model.Users;
import com.company.servise.PdfWriterClass;
import com.company.util.InlineKeyboar;
import com.company.util.ReplyKeyboard;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.company.common.Constants.BOT_TOKEN;
import static com.company.common.Constants.BOT_USERNAME;

public class Bot extends TelegramLongPollingBot {

    public static Users users;
    public static ArrayList<String> subjectsNameList = new ArrayList<>();
    HashMap<String, ArrayList<Integer>> deleteListList = new HashMap<>();


    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            users = SQLServiseBot.getUser(message.getFrom().getId());

            if (!deleteListList.isEmpty() && deleteListList.containsKey(message.getChatId().toString())) {
                deletCallback(message.getChatId().toString());
            }

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);

            if (users.getUserId() != null) {

                if (message.hasText()) {

                    String mysteriousWord = message.getText();

                    for (int i = 0; i < mysteriousWord.length(); i++) {
                        if (mysteriousWord.charAt(i) == '☕') {

                            users.setCurrentSubject(mysteriousWord.substring(0, i - 1));
                            mysteriousWord = mysteriousWord.substring(i);
                            users.setCurrentQuery(mysteriousWord);
                            users.setCurrenTestNumber(1);
                            SQLServiseBot.updateUser();
                            break;
                        }
                    }


                    switch (mysteriousWord) {
                        case "/start":
                        case "Back ↩ ️️":
                            Bot.allNull();

                            SQLServiseBot.updateUser();

                            replyKeyboardMarkup.setKeyboard(ReplyKeyboard.menu());

                            sendMsg(replyKeyboardMarkup, message, "Main menu");

                            break;

                        case "Subjects \uD83D\uDCDA️️":
                            replyKeyboardMarkup.setKeyboard(ReplyKeyboard.subName());
                            sendMsg(replyKeyboardMarkup, message, "Coming soon");
                            break;

                        case "My results \uD83D\uDCCB️️":
                            if (PdfWriterClass.exportHistory()) {

                                replyKeyboardMarkup.setKeyboard(ReplyKeyboard.menu());
                                sendMsg(replyKeyboardMarkup, message, "All your results");
                            } else {
                                replyKeyboardMarkup.setKeyboard(ReplyKeyboard.menu());
                                sendMsg(replyKeyboardMarkup, message, "You haven't taken a test in any subject yet");
                            }
                            break;

                        case "My awards \uD83C\uDFC6️️":

                            HashMap<Integer, String> certificateIdsList = SQLServiseBot.getUserCertificateIds();

                            assert certificateIdsList != null;
                            if (certificateIdsList.size() > 0) {

                                inlineKeyboardMarkup.setKeyboard(InlineKeyboar.userAwards(certificateIdsList));
                                execute(SendMessage.builder()
                                        .text("All your awards")
                                        .chatId(users.getUserId().toString())
                                        .replyMarkup(inlineKeyboardMarkup)
                                        .build());
                            } else {
                                replyKeyboardMarkup.setKeyboard(ReplyKeyboard.menu());
                                sendMsg(replyKeyboardMarkup, message, "You haven't received any awards yet");
                            }
                            break;

                        case "☕️️":
                            String startQuestion = SQLServiseBot.getQuestion(false);
                            users.theyAnswer.clear();
                            SQLServiseBot.updateUser();



                            if (startQuestion != null) {
                                replyKeyboardMarkup.setKeyboard(ReplyKeyboard.backList());
                                inlineKeyboardMarkup.setKeyboard(InlineKeyboar.testSolution());

                                Integer messageId = execute(SendMessage.builder()
                                        .text(startQuestion)
                                        .chatId(message.getChatId().toString())
                                        .replyMarkup(inlineKeyboardMarkup)
                                        .build()).getMessageId();
                                addToDelete(messageId, message.getChatId().toString());

                            } else {
                                SQLServiseBot.updateUser();
                                replyKeyboardMarkup.setKeyboard(ReplyKeyboard.subName());
                                sendMsg(replyKeyboardMarkup, message, "Coming soon");
                            }
                            break;

                        case "/help":
                            sendMsg(message, "I can't help you \uD83D\uDE02");
                            break;
                        case "/setting":
                            sendMsg(message, "The settings cannot be changed \uD83D\uDE21");
                            break;

                    }
                    System.out.println(users + ": " + message.getText());

                }
            } else if (message.hasContact()) {
                Contact contact = message.getContact();
                users = Users.users(message, contact);
                SQLServiseBot.insertUser();

                String name = message.getContact().getFirstName();
                if (message.getContact().getLastName() != null) {
                    name += " " + message.getContact().getLastName();
                }

                replyKeyboardMarkup.setKeyboard(ReplyKeyboard.menu());
                sendMsg(replyKeyboardMarkup, message, "My dear " + name + "!\n" +
                        "You are successfully registered!\n" +
                        "Test your knowledge by solving tests. \n" +
                        "Good luck!");

            } else {
                replyKeyboardMarkup.setKeyboard(ReplyKeyboard.shareContact());
                sendMsg(replyKeyboardMarkup, message, "Welcome to the online test bot.\n" +
                        "Register to start solving the test.\n" +
                        "Send your contact information for this!");
            }
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);

            addToDelete(callbackQuery.getMessage().getMessageId(),
                    callbackQuery.getMessage().getChatId().toString());


            users = SQLServiseBot.getUser(callbackQuery.getFrom().getId());
            if (users != null && users.currentSubject != null
                    && users.currenTestNumber != 0) {
                boolean help = false;

                switch (callbackQuery.getData()) {
                    case "1":
                        users.getTheyAnswer().add("1");
                        users.setCurrenTestNumber(users.getCurrenTestNumber() + 1);
                        break;
                    case "2":
                        users.getTheyAnswer().add("2");
                        users.setCurrenTestNumber(users.getCurrenTestNumber() + 1);
                        break;
                    case "3":
                        users.getTheyAnswer().add("3");
                        users.setCurrenTestNumber(users.getCurrenTestNumber() + 1);
                        break;
                    case "4":
                        users.getTheyAnswer().add("4");
                        users.setCurrenTestNumber(users.getCurrenTestNumber() + 1);
                        break;

                    case "help me plz":

                        Integer point = users.getScore();
                        if (point == null) {
                            point = 0;
                        }

                        inlineKeyboardMarkup.setKeyboard(InlineKeyboar.yesOrNo(true));
                        sendMsg(inlineKeyboardMarkup, callbackQuery,
                                "The cost of knowing the solution to this question is 15 points." + //🪙
                                        " You have " + point + " points. Do you agree to pay 15 points?");

                        return;

                    case "yesss":
                        if (users.getScore() >= 15) {
                            users.setScore(users.getScore() - 15);
                            help = true;
                        } else {
                            inlineKeyboardMarkup.setKeyboard(InlineKeyboar.yesOrNo(false));
                            sendMsg(inlineKeyboardMarkup, callbackQuery, "You don't have enough points to pay");
                            return;
                        }
                        break;
                }

                SQLServiseBot.updateUser();
                String nextQuestion = SQLServiseBot.getQuestion(help);

                if (nextQuestion != null) {
                    inlineKeyboardMarkup.setKeyboard(InlineKeyboar.testSolution());
                    sendMsg(inlineKeyboardMarkup, callbackQuery, nextQuestion);

                } else {
//                    replyKeyboardMarkup.setKeyboard(ReplyKeyboard.subName());
                    PdfWriterClass.exporTestResult();
                    Bot.allNull();

                    execute(EditMessageText.builder()
                            .text("Your result")
                            .chatId(callbackQuery.getMessage().getChatId().toString())
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .build());
                    SQLServiseBot.updateUser();
                }
                System.out.println(users + ": " + callbackQuery.getData());
            }
        }
    }

    private void addToDelete(Integer messageId, String chatId) {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(messageId);
        if (!deleteListList.isEmpty() && deleteListList.containsKey(chatId)) {
            integers.addAll(deleteListList.get(chatId));
        }
        deleteListList.put(chatId, integers);
    }


    @SneakyThrows
    private void deletCallback(String chatId) {
        ArrayList<Integer> integers = deleteListList.get(chatId);
        if (integers.size() > 0) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, integers.get(0));
            execute(deleteMessage);

            for (int i = 1; i < deleteListList.size(); i++) {
                if (!Objects.equals(integers.get(i - 1), integers.get(i))) {
                    deleteMessage = new DeleteMessage(chatId, integers.get(i));
                    execute(deleteMessage);
                }
            }
            deleteListList.remove(chatId);
            System.out.println("deleted");
        }
    }

    @SneakyThrows
    public void sendMsg(Message message, String text) {
        execute(SendMessage.builder()
                .text(text)
                .chatId(message.getChatId().toString())
                .build());
    }

    @SneakyThrows
    public void sendMsg(ReplyKeyboardMarkup replyKeyboardMarkup, Message message, String text) {
        execute(SendMessage.builder()
                .text(text)
                .chatId(message.getChatId().toString())
                .replyMarkup(replyKeyboardMarkup)
                .build());
    }

    @SneakyThrows
    private void sendMsg(InlineKeyboardMarkup inlineKeyboardMarkup, Message message, String text) {
        execute(SendMessage.builder()
                .text(text)
                .chatId(message.getChatId().toString())
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }

    @SneakyThrows
    public void sendMsg(InlineKeyboardMarkup inlineKeyboardMarkup, CallbackQuery callbackQuery, String text) {
        execute(EditMessageText.builder()
                .text(text)
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }


    public void sendMsg(SendDocument sendDocument) {
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static void allNull() {
        users.setCurrenTestNumber(0);
        users.setCurrentSubject(null);
        users.setCurrentQuery(null);
        users.getTheyAnswer().clear();
    }
}
