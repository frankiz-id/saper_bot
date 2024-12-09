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
    public final Queue<Update> receiveQueue = new ConcurrentLinkedQueue<>();
    private boolean working = false;
    private HashMap<String, User> users = new HashMap<>();
    private Field field;

    @Override
    public void onUpdateReceived(Update update) {
        if(working){
            receiveQueue.add(update);
        }
        else{
            working = true;
            executeUpdate(update);
        }
    }

    private void executeUpdate(Update update){
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                String chatId = String.valueOf(update.getMessage().getChatId());
                users.put(chatId, new User(chatId));
                SendMessage msg = new SendMessage();
//                    int messageId = update.getMessage().getMessageId();
//                    msg.setText(ModeSelection);
//                    msg.setChatId(update.getMessage().getChatId());
//                    msg.setReplyMarkup(getInlineKeyboardButton(chatId, messageId));
//                    execute(msg);
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
        deleteLastMessage(chatId, messageId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(Training);
        inlineKeyboardButton1.setCallbackData("-hОбучение");
        inlineKeyboardButton2.setText(Standart);
        inlineKeyboardButton2.setCallbackData("-sСтандартный");
        inlineKeyboardButton3.setText(RealTime);
        inlineKeyboardButton3.setCallbackData("-rреальное_время");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
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
                message.setText("Сработал Обучение");
                message.setChatId(chatId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "-s":
                //по идее нам реализацию каждой из функций для обработки режимов нужно сделать в отдельном файле/классе с логикой бота
                //field = new Field(12, 8);
                users.get(chatId).setMyField(12, 8);
                playStandart(chatId, messageId);
                break;
            case "-r":
                message.setText("Сработал реальное_время");
                message.setChatId(chatId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            //срабатывание кнопки из поля
            case "-b":
                String comand = callData.substring(2);
                if (comand.equals("Флаг")) {
                    users.get(chatId).getMyField().setMode(false);
                    working = false;
                    if (!receiveQueue.isEmpty()){
                        onUpdateReceived(receiveQueue.poll());
                    }
                } else if (comand.equals("Копать")) {
                    users.get(chatId).getMyField().setMode(true);
                    working = false;
                    if (!receiveQueue.isEmpty()){
                        onUpdateReceived(receiveQueue.poll());
                    }
                    //System.out.println(users.get(chatId).getMyField().getMode());
                }
                //пришла кнопка игрового поля
                else {
                    if (users.get(chatId).getMyField().getIsEndGame()) {
                        SendMessage msg = new SendMessage();
//                            msg.setText(ModeSelection);
//                            msg.setChatId(chatId);
//                            msg.setReplyMarkup(getInlineKeyboardButton(chatId, messageId));
//                            execute(msg);
                        sendMessage(msg, ModeSelection, chatId, getInlineKeyboardButton(chatId, messageId));
                        break;
                    }
                    //System.out.println(callData.substring(2));
                    users.get(chatId).getMyField().updateData(comand);
                    if (users.get(chatId).getMyField().touchButtonByName(comand).isBomb()
                            && !users.get(chatId).getMyField().touchButtonByName(comand).getFlag()) {
                        users.get(chatId).getMyField().showBombs();
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
        working = false;
        if (!receiveQueue.isEmpty()){
            onUpdateReceived(receiveQueue.poll());
        }
    }

    private void playStandart(String chatId, int messageId) {
        deleteLastMessage(chatId, messageId);
        SendMessage message = new SendMessage();
//        message.setText(CountMines);
//        message.setChatId(chatId);
//        InlineKeyboardMarkup inlineKeyboardMarkup = field.getInlineKeyboardButton();
//        message.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage(message, CountMines, chatId, users.get(chatId).getMyField().getInlineKeyboardButton());
    }
}