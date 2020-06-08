package com.example.a92gde.chatapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Gesture implements Serializable {

    public static final long serialVersionUID = 1L;
    private ArrayList<Box> gesture;
    private String color;

    public Gesture(){
        gesture = new ArrayList<Box>();
        color="Red";
    }

    public Gesture(Gesture g){

        this.gesture = new ArrayList<Box>(g.gesture);
        this.color = g.color;
    }


    public Gesture(String co){
        gesture = new ArrayList<Box>();
        color=co;
    }

    public void setColor(String co){
        color=co;
    }

    public void addBox(Box box){
        gesture.add(box);
    }

    public ArrayList<Box> getGesture(){
        return gesture;
    }

    public int getNumberOfBoxes(){
        return gesture.size();
    }

    public String getColor(){
        return color;
    }

    public boolean isIn(int row, int column){
        for (Box b:gesture){
            if ((b.getRow()==row) && (b.getColumn()==column)){
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty(){
        if (this.getNumberOfBoxes()==0){
            return true;
        }
        else{
            return false;
        }
    }

    public ArrayList<Box> getBoxes(){ return gesture;}
}
