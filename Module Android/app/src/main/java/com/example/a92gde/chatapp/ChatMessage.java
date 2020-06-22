package com.example.a92gde.chatapp;

//Class to fill the content of the ChatArrayAdapter
public class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        //determine if the message belong to the left or the right of the screen
        this.left = left;
        this.message = message;
    }
}