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
                    int messageId = update.getMessage().getMessageId();
                    String chatId = String.valueOf(update.getMessage().getChatId());
                    msg.setText(ModeSelection);
                    msg.setChatId(update.getMessage().getChatId());
                    msg.setReplyMarkup(get_InlineKeyboardButton(chatId, messageId));
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

    public InlineKeyboardMarkup get_InlineKeyboardButton(String chatId, int messageId)
    {
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

    private void handleCallbackQuery(Update update)
    {
        CallbackQuery currentCallback = update.getCallbackQuery();
        String callData = currentCallback.getData();
        //System.out.println(callData);
        String callDataCase = callData.substring(0, 2);
        //System.out.println(callDataCase);

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
                field = new Field(12, 8);
                System.out.println(field.getMode());
                playStandart(chatId, messageId);
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
                String comand = callData.substring(2);
                if (comand.equals("Флаг")){
                    field.setMode(false);
                    System.out.println(field.getMode());
                }
                else if (comand.equals("Копать")){
                    field.setMode(true);
                    System.out.println(field.getMode());
                }
                //пришла кнопка игрового поля
                else{
                    if(field.getIsEndGame()){
                        try {
                            SendMessage msg = new SendMessage();
                            msg.setText(ModeSelection);
                            msg.setChatId(chatId);
                            msg.setReplyMarkup(get_InlineKeyboardButton(chatId, messageId));
                            execute(msg);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    break;
                    }
                    //System.out.println(callData.substring(2));
                    field.updateData(comand);
                    if(field.touchButtonByName(comand).isBomb()){
                        field.bomber();
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

    private void playStandart(String chatId, int messageId){
        deleteLastMessage(chatId, messageId);
        SendMessage message = new SendMessage();
        message.setText(CountMines);
        message.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = field.getInlineKeyboardButton();
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}