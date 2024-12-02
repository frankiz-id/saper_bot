package org.example;

public class Button {
    private int property;
    private boolean open;
    private boolean flag;

    public Button(){
        this.property = 0;
        this.open = false;
        this.flag = false;
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
        if (this.getFlag()){ return "\uD83D\uDEA9"; } // флажок
        if (!(this.getOpen())){ return "\uD83D\uDEAB"; } // закрытое поле
        int prop = property;
        return switch (prop) {
            case 0 -> "\uFE0F\uFE0F"; //пустое поле
            case 1, 2, 3, 4, 5, 6, 7, 8 -> String.valueOf(prop);
            case 9 -> "\uD83D\uDCA5"; //бомба
            default -> "";
        };
    }
}

// "1"; //эмодзи для кол-ва бомб вокруг
// "\uD83D\uDEA9"; //эмодзи флажка
// "\uD83D\uDCA3"; //эмодзи бомбы
// "\uD83D\uDCA5"; //эмодзи взрыва
// "\u200B"; //эмодзи пустого поля
// "\uD83D\uDEAB"; //эмодзи закрытого поля
