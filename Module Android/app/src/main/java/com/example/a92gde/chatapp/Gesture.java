package com.example.a92gde.chatapp;

import java.util.ArrayList;

public class Gesture {
    private ArrayList<Box> gesture;
    private String color;

    public Gesture(){
        gesture = new ArrayList<Box>();
        color="Red";
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
}
