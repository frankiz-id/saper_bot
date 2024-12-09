package org.example;

import org.example.Field;

public class User {

    private Field myField;
    private String myChatID;

    public User(String myChatID){
        this.myChatID = myChatID;
    }

    public void setMyField(int rows, int cols) {
        this.myField = new Field(rows, cols);
    }
    public Field getMyField(){
        return this.myField;
    }

    public void setMyChatID(String myChatID) {
        this.myChatID = myChatID;
    }
    public String getMyChatID(){
        return this.myChatID;
    }
}
