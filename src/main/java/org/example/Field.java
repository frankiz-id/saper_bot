package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.*;
import java.lang.reflect.Array;
import org.example.Button;
import javax.validation.constraints.Null;


public class Field {
    private Button[][] buttons;
    private int countRow;
    private int countCol;
    private boolean isEndGame;
    private boolean startedGame;

    public Field(int countRow, int countCol){
        this.isEndGame = false;
        this.startedGame = false;
        this.countRow = countRow;
        this.countCol =countCol;
        this.buttons = new Button[countRow][countCol];
        for (int i = 0; i < countRow; i++) {
            for (int j = 0; j < countCol; j++) {
                buttons[i][j] = new Button(); // Инициализация с координатами (i, j)
            }
        }
    }

    public void setIsEndGame(boolean isEndGame){
        this.isEndGame = isEndGame;
    }
    public boolean getIsEndGame(){
        return isEndGame;
    }
    public void setStartedGame(boolean startedGame){
        this.startedGame = startedGame;
    }
    public boolean getStartedGame(){
        return startedGame;
    }
    public int getCountRow() {
        return countRow;
    }
    public void setCountRow(int countRow) {
        this.countRow = countRow;
    }
    public int getCountCol() {
        return countCol;
    }
    public void setCountCol(int countCol) {
        this.countCol = countCol;
    }
    public Button[][] getButtons(){return buttons;}


    private int[] coordMaker(){
        Random random = new Random();
        int bomb_y_coord = random.nextInt(this.getCountRow());
        int bomb_x_coord = random.nextInt(this.getCountCol());
        return new int []{bomb_y_coord, bomb_x_coord};
    }

    //открывает все клетки вокруг данной
    private void opener(int y_coord, int x_coord){
        Button bt = buttons[y_coord][x_coord];
        buttons[y_coord][x_coord].setOpen(true);
        if (bt.isBomb()){
            setIsEndGame(true);
            return;
        }
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                int current_button_y = y_coord-1+i;
                int current_button_x = x_coord-1+j;
                boolean currentButtonOpen = buttons[current_button_y][current_button_x].getOpen();
                if(current_button_y < this.getCountRow() && current_button_y > -1
                        && current_button_x < this.getCountCol() && current_button_x > -1
                        && !currentButtonOpen) {
                    if(bt.getFlag()){
                        continue;
                    }
                    if(bt.getProperty() == 0){
                        opener(y_coord, x_coord);
                    }
                    else{
                        buttons[y_coord][x_coord].setOpen(true);
                    }
                }
            }
        }
    }

    public void updateData(String buttonName, boolean mode){

        int y_coord = Integer.parseInt(buttonName.substring(0, 2));
        int x_coord = Integer.parseInt(buttonName.substring(2));

        if (!getStartedGame()){
            setStartedGame(true);
            int countBombs = (int) Math.ceil(this.getCountRow() * this.getCountCol() * 17.0 / 100); //17% поля - бомбы
            int [] place;

            while (countBombs > 0) {
                place = coordMaker();
                if(Math.abs(place[1] - x_coord) > 1 && Math.abs(place[0] - y_coord) > 1
                        && !buttons[place[0]][place[1]].isBomb()){
                    buttons[place[0]][place[1]].setProperty(9);
                    for (int i = 0; i < 3; i++){
                        for (int j = 0; j < 3; j++){
                            int current_button_y = place[0]-1+i;
                            int current_button_x = place[1]-1+j;
                            int property = buttons[current_button_y][current_button_x].getProperty();
                            if(current_button_y < this.getCountRow() && current_button_y > -1
                                    && current_button_x < this.getCountCol() && current_button_x > -1
                                    && property != 9){
                                buttons[current_button_y][current_button_x].setProperty(property+1);
                            }
                        }
                    }
                    countBombs -= 1;
                }
            }
            opener(y_coord, x_coord);
        }
        else{
            Button bt = buttons[y_coord][x_coord];
            boolean curFlag = bt.getFlag();
            //mode 1 - открывать, mode 0 - флажок
            if (mode){
                if(bt.getOpen()){
                    int counterFlags = 0;
                    for (int i = 0; i < 3; i++){
                        for (int j = 0; j < 3; j++){
                            int current_button_y = y_coord-1+i;
                            int current_button_x = x_coord-1+j;
                            if(current_button_y < this.getCountRow() && current_button_y > -1
                                    && current_button_x < this.getCountCol() && current_button_x > -1
                                    && i != 1 && j != 1){
                                counterFlags += 1;
                            }
                        }
                    }
                    if (counterFlags == bt.getProperty()){
                        opener(y_coord, x_coord);
                    }
                }
                else{
                    buttons[y_coord][x_coord].setFlag(!curFlag);
                    if (bt.isBomb()){
                        setIsEndGame(true);
                        return;
                    }
                    if (bt.getProperty() == 0){
                        opener(y_coord, x_coord);
                    }
                    else{
                        buttons[y_coord][x_coord].setOpen(true);
                    }
                }
            }
            else{
                //флажок можно установить/снять только на закрытую
                if (!bt.getOpen()){
                    buttons[y_coord][x_coord].setFlag(!curFlag);
                }
            }
        }
    }

    public InlineKeyboardMarkup getInlineKeyboardButton(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int rowsCount = this.getCountRow(); // 12 строк
        int colsCount = this.getCountCol();  // 8 столбцов
        for (int i = 0; i < rowsCount; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < colsCount; j++) {
                String buttonText = buttons[i][j].getEmoji();
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
        // Устанавливаем строки в клавиатуру
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    //Получает данные о кнопке по координатам
    //Зачем оно нам, если внутри Field мы и так имеем доступ к buttons?
    private Button touchButtonByName(String buttonName){
        int y_coord = Integer.parseInt(buttonName.substring(0, 2));
        int x_coord = Integer.parseInt(buttonName.substring(2));

        return buttons[y_coord][x_coord];
    }
}
