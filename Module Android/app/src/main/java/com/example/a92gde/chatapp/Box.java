package com.example.a92gde.chatapp;

public class Box {
    int column;
    int row;

    public Box(){
        column=0;
        row=0;
    }

    public Box(int c, int r){
        column=c;
        row=r;
    }


    public void setColumn(int c){
        column=c;
    }

    public void setRow(int r){
        row=r;
    }

    public int getRow(){
        return row;
    }

    public int getColumn(){
        return column;
    }

}
