package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.example.Constant.*;

public class Bot extends TelegramLongPollingBot {
    private HashMap<String, User> users = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                String chatId = String.valueOf(update.getMessage().getChatId());
                users.put(chatId, new User(chatId));
                SendMessage msg = new SendMessage();
                sendMessage(msg, ModeSelection, chatId, getInlineKeyboardButton(chatId, update.getMessage().getMessageId()));
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv("botUserName");
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

    public InlineKeyboardMarkup getInlineKeyboardButton(String chatId, int messageId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(Training);
        inlineKeyboardButton1.setCallbackData("-hОбучение");
        inlineKeyboardButton2.setText(Standart);
        inlineKeyboardButton2.setCallbackData("-sСтандартный");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getKeyboardForTraining(String chatId, int messageId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(Easy);
        inlineKeyboardButton1.setCallbackData("-eЛегкий");
        inlineKeyboardButton2.setText(Standart);
        inlineKeyboardButton2.setCallbackData("-sСтандартный");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private void handleCallbackQuery(Update update) {
        CallbackQuery currentCallback = update.getCallbackQuery();
        String callData = currentCallback.getData();
        String callDataCase = callData.substring(0, 2);

        int messageId = currentCallback.getMessage().getMessageId();
        String chatId = Long.toString(currentCallback.getMessage().getChatId());
        SendMessage message = new SendMessage();
        switch (callDataCase) {
            case "-h":
                deleteLastMessage(chatId, messageId);
                sendMessage(message, Rules, chatId, getKeyboardForTraining(chatId, messageId));
                break;
            case "-s":
                //по идее нам реализацию каждой из функций для обработки режимов нужно сделать в отдельном файле/классе с логикой бота
                users.get(chatId).setMyField(12, 8);
                playStandart(chatId, messageId);

                break;
            case "-e":
                //по идее нам реализацию каждой из функций для обработки режимов нужно сделать в отдельном файле/классе с логикой бота
                users.get(chatId).setMyField(5, 5);
                playStandart(chatId, messageId);

                break;

            //срабатывание кнопки из поля
            case "-b":

                String comand = callData.substring(2);
                if (comand.equals("Флаг")) {
                    users.get(chatId).getMyField().setMode(false);
                } else if (comand.equals("Копать")) {
                    users.get(chatId).getMyField().setMode(true);
                }
                //пришла кнопка игрового поля
                else {
                    users.get(chatId).getMyField().updateData(comand);
                    if (users.get(chatId).getMyField().getCountClosedCells() == users.get(chatId).getMyField().getCountBombs()){
                        deleteLastMessage(chatId, messageId);
                        goodEndGame(users.get(chatId).getMyField(), chatId);
                        break;
                    }
                    if (users.get(chatId).getMyField().getIsEndGame()){
                        deleteLastMessage(chatId, messageId);
                        badEndGame(users.get(chatId).getMyField(), chatId);
                        break;
                    }
                    playStandart(chatId, messageId);
                }

        }
    }

    private void deleteLastMessage(String chatId, int messageId) {
        {
            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId).build();
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(SendMessage message, String text, String chatID, InlineKeyboardMarkup inlineKeyboardMarkup){
        message.setText(text);
        message.setChatId(chatID);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void playStandart(String chatId, int messageId) {
        deleteLastMessage(chatId, messageId);
        SendMessage message = new SendMessage();
        sendMessage(message, CountMines+users.get(chatId).getMyField().getCountBombs(), chatId, getInlineKeyboardGame(users.get(chatId).getMyField()));
    }

    public InlineKeyboardMarkup getInlineKeyboardGame(Field field){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < field.getCountRow(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < field.getCountCol(); j++) {
                String buttonText = field.getButtons()[i][j].getEmoji();
                String callbackData = String.format("-b%02d%02d", i, j);
                // Создаем кнопку
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .text(buttonText)
                        .callbackData(callbackData)
                        .build();
                // Добавляем кнопку в строку
                row.add(button);
            }
            // Добавляем строку в клавиатуру
            rows.add(row);
        }
        List<InlineKeyboardButton> rowFlagDig = new ArrayList<>();
        InlineKeyboardButton buttonFlag = new InlineKeyboardButton();
        buttonFlag.setText(ModeFlag);
        buttonFlag.setCallbackData("-bФлаг");
        InlineKeyboardButton buttonDig = new InlineKeyboardButton();
        buttonDig.setText(ModeDig);
        buttonDig.setCallbackData("-bКопать");
        rowFlagDig.add(buttonFlag);
        rowFlagDig.add(buttonDig);
        rows.add(rowFlagDig);
        // Устанавливаем строки в клавиатуру
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private void badEndGame(Field field, String chatID){
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Вы проиграли\nРасположение мин:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < field.getCountRow(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < field.getCountCol(); j++) {
                if (field.getButtons()[i][j].isBomb()){
                    field.getButtons()[i][j].setOpen(true);
                }
                String buttonText = field.getButtons()[i][j].getEmoji();
                String callbackData = String.format("NoneData", i, j);
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .text(buttonText)
                        .callbackData(callbackData)
                        .build();
                row.add(button);
            }
            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);

        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void goodEndGame(Field field, String chatID){
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Вы выиграли\nРасположение мин:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < field.getCountRow(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < field.getCountCol(); j++) {
                if (field.getButtons()[i][j].getOpen()){
                    field.getButtons()[i][j].setProperty(0);
                }
                else{
                    field.getButtons()[i][j].setOpen(true);
                    field.getButtons()[i][j].setFlag(false);
                    field.getButtons()[i][j].setProperty(10);
                }
                String buttonText = field.getButtons()[i][j].getEmoji();
                String callbackData = String.format("NoneData", i, j);
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .text(buttonText)
                        .callbackData(callbackData)
                        .build();
                row.add(button);
            }
            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);

        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}