package org.example;

public class Button {
    private int property;
    private boolean open;
    private boolean flag;

    public Button(){
        this.property = 0;
        this.open = false;
    }

    public void setProperty(int property){
        this.property = property;
    }
    public int getProperty(){
        return property;
    }
    public void setOpen(boolean open){
        this.open = open;
    }
    public boolean getOpen(){
        return open;
    }
    public void setFlag(boolean flag){
        this.flag = flag;
    }
    public boolean getFlag(){
        return flag;
    }

    public boolean isBomb(){
        return property == 9;
    }

    public String getEmoji(){
        if (getFlag()){ return "\uD83D\uDEA9"; }
        return switch (property){
            case 0 -> "\uFE0F\uFE0F";
            case 1, 2, 3, 4, 5, 6, 7, 8 -> String.valueOf(property);
            case 9 -> "\uD83D\uDCA3";
            default -> null;
        };
    }
}

// "1"; //эмодзи для кол-ва бомб вокруг
// "\uD83D\uDEA9"; //эмодзи флажка
// "\uD83D\uDCA3"; //эмодзи бомбы
// "\uD83D\uDCA5"; //эмодзи взрыва
// "\u200B"; //эмодзи пустого поля
// "⬜\uFE0F"; //эмодзи закрытого поля
