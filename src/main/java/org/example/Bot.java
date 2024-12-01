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

import static org.example.Constant.*;

public class Bot extends TelegramLongPollingBot {
    Field field;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            if(update.getMessage().getText().equals("/start")){
                try {
                    SendMessage msg = new SendMessage();
                    msg.setText(ModeSelection);
                    msg.setChatId(update.getMessage().getChatId());
                    msg.setReplyMarkup(get_InlineKeyboardButton());
                    execute(msg);
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

    public static InlineKeyboardMarkup get_InlineKeyboardButton()
    {
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

    private void handleCallbackQuery(Update update)
    {
        CallbackQuery currentCallback = update.getCallbackQuery();
        String callData = currentCallback.getData();
        String callDataCase = callData.substring(0, 2);
        int messageId = currentCallback.getMessage().getMessageId();
        String chatId = Long.toString(currentCallback.getMessage().getChatId());
        SendMessage message = new SendMessage();
        switch (callDataCase)
        {
            case "-h":
                message.setText("Сработал Обучение");
                message.setChatId(chatId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                break;
            case "-s" :
                //по идее нам реализацию каждой из функций для обработки режимов нужно сделать в отдельном файле/классе с логикой бота
                startStandart(chatId, messageId);
                break;
            case "-r" :
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

    private void startStandart(String chatId, int messageId){
        SendMessage message = new SendMessage();
        deleteLastMessage(chatId, messageId);
        message.setText(CountMines);
        message.setChatId(chatId);
        field = new Field(12, 8);
        InlineKeyboardMarkup inlineKeyboardMarkup = field.getInlineKeyboardButton();
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}