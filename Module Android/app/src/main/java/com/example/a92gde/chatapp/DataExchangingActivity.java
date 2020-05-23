package com.example.a92gde.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class DataExchangingActivity extends AppCompatActivity implements AsyncResponse{

    private ImageView imageView;
    private static String separator = ";";
    private static String endseparator = ".";
    private static String username;

    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        username = intent.getStringExtra("user");
        ListView chatListView = (ListView) findViewById(R.id.chatListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(arrayAdapter);

    }

    // Send text button pressed
    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.chatEditText);
        final String messageContent = chatEditText.getText().toString();

        sendMessageToServer("MSGREQUEST"+ separator+messageContent);
        Log.i("Info", "Message sent to server (code): " + "MSGREQUEST");

        // We will receive success if the data was successfully received/stored by server, else fail
        getMessageFromServer();

        chatEditText.setText("");

        messages.add(messageContent);
        arrayAdapter.notifyDataSetChanged();


    }

    public void sendMessageToServer(String message) {


        ClientActivity.TextSendingTask textSendingTask = new ClientActivity().new TextSendingTask();
        textSendingTask.execute(message);

    }


    public void getMessageFromServer () {


        ClientActivity.MessageGettingTask messageGettingTask = new ClientActivity().new MessageGettingTask();

        messageGettingTask.delegate = this;

        String output[] = new String[10];
        messageGettingTask.execute(output);
        // results will be in the output postexecute

    }

    @Override
    public void processFinish(String output) {
        // Here we receive the result fired from async class
        // of onPostExecute(result) method.
        Log.i("CLIENT-SERVER", "Server message: " + output);

        messages.add("Server: "+output);
        arrayAdapter.notifyDataSetChanged();

    }

    public void drawGesture(View view){
        Intent gesture = new Intent(DataExchangingActivity.this,ColorChooser.class );
        startActivity(gesture);
    }

}
