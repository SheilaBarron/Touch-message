package com.example.a92gde.chatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//The chatArrayAdapter is a class usefull to display the text message

public class ChatArrayAdapter extends ArrayAdapter {//This class is a particular array adapter that allows us to have bubble messages

    private TextView chatText;
    private List chatMessageList = new ArrayList();
    private LinearLayout singleMessageContainer;

    //Add a message in the Chat List
    public void add(ChatMessage object)
    {
        chatMessageList.add(object);
        super.add(object);
    }

    //Creator
    public ChatArrayAdapter(Context context, int item, ArrayList<String> messages)
    {
        super(context, item, messages);
    }

    //Return the umber of Messages in the list
    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return (ChatMessage)this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.message);
        //set the message into pretty bubble containers
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_b : R.drawable.bubble_a);
        //set the bottom gravity to see constantly the last message sent
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    //Methode to decode a bite array into a bite map
    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
