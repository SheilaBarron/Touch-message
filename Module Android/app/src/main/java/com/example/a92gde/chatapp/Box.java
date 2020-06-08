package com.example.a92gde.chatapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Box implements Serializable {

    public static final long serialVersionUID = 1L;
    int column;
    int row;
    int time;

    public Box(){
        column=0;
        row=0;
        time=0;
    }

    public Box(int r, int c, int t){
        column=c;
        row=r;
        time = t;
    }

    public Box(Box b){

        this.column = b.column;
        this.row = b.row;
        this.time = b.time;

    }


    public void setColumn(int c){
        column=c;
    }

    public void setRow(int r){
        row=r;
    }

    public void setTime(int t){time =t;}

    public int getRow(){
        return row;
    }

    public int getColumn(){
        return column;
    }

    public int getTime(){return time;}

}
