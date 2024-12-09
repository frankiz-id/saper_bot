package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.*;
import static org.example.Constant.*;
import java.lang.reflect.Array;
import org.example.Button;
import javax.validation.constraints.Null;


public class Field {
    private Button[][] buttons;
    private int countRow;
    private int countCol;
    private boolean isEndGame;
    private boolean startedGame;
    //false - флаг, true - копать
    private boolean mode;

    public Field(int countRow, int countCol){
        this.isEndGame = false;
        this.startedGame = false;
        this.mode = true;
        this.countRow = countRow;
        this.countCol =countCol;
        this.buttons = new Button[countRow][countCol];
        for (int i = 0; i < countRow; i++) {
            for (int j = 0; j < countCol; j++) {
                buttons[i][j] = new Button();
            }
        }
    }

    public void setIsEndGame(boolean isEndGame){
        this.isEndGame = isEndGame;
    }
    public boolean getIsEndGame(){
        return isEndGame;
    }
    public void setMode(boolean mode){
        this.mode = mode;
    }
    public boolean getMode(){
        return mode;
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


    private int[] generateCoord(){
        Random random = new Random();
        int bomb_y_coord = random.nextInt(this.getCountRow());
        int bomb_x_coord = random.nextInt(this.getCountCol());
        //System.out.println(bomb_y_coord);

        return new int[]{bomb_y_coord, bomb_x_coord};
    }


    private void openCells(int y_coord, int x_coord){
        buttons[y_coord][x_coord].setOpen(true);
        if (buttons[y_coord][x_coord].isBomb()){
            setIsEndGame(true);
            return;
        }
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0){
                    continue;
                }
                int curY = y_coord + i;
                int curX = x_coord + j;
                if (curY >= 0 && curY < 12 && curX >= 0 && curX < 8 && !buttons[curY][curX].getFlag() && !buttons[curY][curX].getOpen()){
                    if(buttons[curY][curX].getProperty() == 0){
                        openCells(curY, curX);
                    }
                    else{
                        buttons[curY][curX].setOpen(true);
                    }
                }
            }
        }
    }

    private void startGame(int y_coord, int x_coord){
        this.setStartedGame(true);
        int countBombs = (int) Math.ceil(this.getCountRow() * this.getCountCol() * 16.0 / 100); //16% поля - бомбы
        //int countBombs = 16; //16% поля - бомбы

        while (countBombs != 0) {
            int[] place = generateCoord();
            //System.out.print(place[0]);
            //System.out.println(" " + place[1]);
            if((Math.abs(place[0] - y_coord) > 1 || Math.abs(place[1] - x_coord) > 1) && (!(buttons[place[0]][place[1]].isBomb()))){
                buttons[place[0]][place[1]].setProperty(9);
                countBombs -= 1;
            }
        }
        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 8; j++) {
                if (buttons[i][j].isBomb()){
                    for (int y = -1; y <= 1; y++){
                        for (int x = -1; x <= 1; x++){
                            if (y == 0 && x == 0){
                                continue;
                            }
                            int curY = i + y;
                            int curX = j + x;
                            if (curY >= 0 && curY < 12 && curX >= 0 && curX < 8 && !buttons[curY][curX].isBomb()){
                                buttons[curY][curX].setProperty(buttons[curY][curX].getProperty()+1);
                            }
                        }
                    }
                }
            }
        }
        openCells(y_coord, x_coord);
    }

    public void updateData(String buttonName){

        int y_coord = Integer.parseInt(buttonName.substring(0, 2));
        int x_coord = Integer.parseInt(buttonName.substring(2));
        if (!getStartedGame()){
            startGame(y_coord, x_coord);
        }
        else{
            //mode 1 - открывать, mode 0 - флажок
            if (mode){
                if(buttons[y_coord][x_coord].getOpen()){
                    int counterFlags = 0;
                    for (int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if (i == 0 && j == 0){
                                continue;
                            }
                            int curY = y_coord + i;
                            int curX = x_coord + j;
                            if (curY >= 0 && curY < 12 && curX >= 0 && curX < 8 && buttons[curY][curX].getFlag()){
                                counterFlags += 1;
                            }
                        }
                    }
                    if (counterFlags == buttons[y_coord][x_coord].getProperty()){
                        openCells(y_coord, x_coord);
                    }
                }
                else{
                    buttons[y_coord][x_coord].setFlag(false);
                    if (buttons[y_coord][x_coord].isBomb()){
                        setIsEndGame(true);
                        return;
                    }
                    if (buttons[y_coord][x_coord].getProperty() == 0){
                        openCells(y_coord, x_coord);
                    }
                    else{
                        buttons[y_coord][x_coord].setOpen(true);
                    }
                }
            }
            else{
                //флажок можно установить/снять только на закрытую
                if (!buttons[y_coord][x_coord].getOpen()){

                    buttons[y_coord][x_coord].setFlag(!buttons[y_coord][x_coord].getFlag());

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

    public void showBombs(){
        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 8; j++) {
                if (buttons[i][j].isBomb()){
                    buttons[i][j].setOpen(true);
                }
            }
        }
    }



    //Получает данные о кнопке по координатам
    public Button touchButtonByName(String buttonName){
        int y_coord = Integer.parseInt(buttonName.substring(0, 2));
        int x_coord = Integer.parseInt(buttonName.substring(2));

        return buttons[y_coord][x_coord];
    }
}
