package org.example;

public class Button {
    private int property;
    private boolean open;
    private boolean flag;
    //мина, число, закрыто,

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
}
