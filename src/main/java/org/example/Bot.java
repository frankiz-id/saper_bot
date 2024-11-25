package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            if(update.getMessage().getText().equals("/start")){
                try {
                    SendMessage msg = new SendMessage();
                    msg.setText("Выбор режима");
                    msg.setChatId(update.getMessage().getChatId());
                    execute(msg);
                    execute(sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else if(update.hasCallbackQuery()){
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

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setText("Новая информация, hrththrок");
        message.setChatId(chatId);

        InlineKeyboardMarkup inlineKeyboardMarkup = get_InlineKeyboardButton();
        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }

    public static InlineKeyboardMarkup get_InlineKeyboardButton()
    {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Обучение");
        inlineKeyboardButton1.setCallbackData("Обучение");
        inlineKeyboardButton2.setText("Стандартный");
        inlineKeyboardButton2.setCallbackData("Стандартный");
        inlineKeyboardButton3.setText("Реальное время");
        inlineKeyboardButton3.setCallbackData("реальное_время");
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

    private void handleCallbackQuery(Update update)
    {
        CallbackQuery currentCallback = update.getCallbackQuery();
        String call_data = currentCallback.getData();
        int messageId = currentCallback.getMessage().getMessageId();
        String chatId = Long.toString(currentCallback.getMessage().getChatId());
        SendMessage message = new SendMessage();
        switch (call_data)
        {
            case "Обучение":
                message.setText("Сработал Обучение");
                message.setChatId(chatId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "Стандартный" :
                deleteLastMessage(chatId, messageId);
                Field field = new Field();
                message.setText("Количество оставшихся мин: ");
                message.setChatId(chatId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "реальное_время" :
                message.setText("Сработал реальное_время");
                message.setChatId(chatId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
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
                throw new RuntimeException(e);
            }
        }
    }
}