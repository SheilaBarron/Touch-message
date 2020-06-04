package com.example.a92gde.chatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DataExchangingActivity extends AppCompatActivity implements AsyncResponse{

    final Handler handler = new Handler();
    private boolean end = false;
    private static final int SERVER_PORT = 8010;
    private static final String SERVER_IP = "10.30.220.126"; // mease

    // private static final String SERVER_IP = "192.168.1.64"; // ju lille
    ObjectOutputStream objectOutputStream;
    //DataOutputStream dout;
    ObjectInputStream objectInputStream;
    //DataInputStream din;
    Socket socketChars;

    private ImageView imageView;

    private static String separator = " ";
    //private static String separator = ";";

    private static String username;
    private Gesture gesture;

    ArrayList<String> messages = new ArrayList<>();
    public ListView chatListView;
    public ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            username = intent.getStringExtra("user");
        }

        Log.i("Info", "NAME OF USER:"+username);

        chatListView = (ListView) findViewById(R.id.chatListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(arrayAdapter);

        ChatTask chatTask = new ChatTask();
        chatTask.delegate = this;
        chatTask.execute();

        //String messageToSend = username;
        //sendMessageToServer(username);
        /*
        String response = null;
        try {
            response = objectInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Info", "LOGIN RESPONSE FROM SERVER: " + response);
        */



        // QUAND TU AS RÉCUPÉRÉ LE GESTE, IL FAUT EXÉCUTER CES TROIS LIGNES
        /*Intent intent_test = new Intent(DataExchangingActivity.this,ShowingGestureActivity.class );
        intent_test.putExtra("gesture", gesture);
        startActivity(intent_test);*/


    }
    

    @Override
    public void onBackPressed() {

        sendMessageToServer(username+separator+"LOGOUT");
        super.onBackPressed();
    }


    public class ChatTask extends AsyncTask<String, Void, String> {

        // In order to get a return message
        public AsyncResponse delegate = null;

        @Override
        protected String doInBackground(String... params) {

            try {
                socketChars = new Socket(SERVER_IP, SERVER_PORT); // Connect to the socket

                Log.i("Info", "CONNECTING TO SERVER");

                OutputStream outputStream = socketChars.getOutputStream();
                objectOutputStream = new ObjectOutputStream(outputStream);
                //dout = new DataOutputStream(socketChars.getOutputStream());

                InputStream inputStream = socketChars.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);
                //din = new DataInputStream(socketChars.getInputStream());

                //sendMessageToServer(username);

                if(objectInputStream == null || objectOutputStream == null) {

                    Log.i("Info", "NULL stream");
                }

                sendMessageToServer(username);

                /*
                String response = null;
                try {
                    response = objectInputStream.readObject().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("Info", "LOGIN RESPONSE FROM SERVER: " + response);

                 */
                startServerSocket();


            } catch (IOException  e) { //| ClassNotFoundException e
                e.printStackTrace();
            }

            return null;
        }



        /*

        protected void onPause(){
            this.cancel(false);
        }
        */

        protected void onPostExecute(String result) {

            delegate.processFinish(result);

        }


    }

    private void startServerSocket() {


        Thread thread = new Thread(new Runnable() {

            private String stringData = null;

            @Override
            public void run() {

                Log.i("Info", "We started the server connexion");

                try {

                    while (!end) {

                        stringData = objectInputStream.readObject().toString();
                        //stringData = objectInputStream.readUTF();
                        //stringData = din.readUTF();

                        if (stringData != null) {

                            //messages.add("Server: " + stringData);
                            //arrayAdapter.notifyDataSetChanged();
                            updateUI(stringData);

                            Log.i("Info", "FROM SERVER - " + stringData);

                        }


                    }

                    //socket.close();

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            });

        thread.start();

    }

    private void updateUI(final String stringData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                //String s = textViewDataFromClient.getText().toString();
                if (stringData.trim().length() != 0){
                    messages.add(stringData);
                    arrayAdapter.notifyDataSetChanged();
                }

                    //textViewDataFromClient.setText(s + "\n" + "From Client : " + stringData);

            }
        });
    }


    // Send text button pressed
    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.chatEditText);
        final String messageContent = chatEditText.getText().toString();

        //sendMessageToServer("MSGREQUEST"+ separator+messageContent);
        String messageToSend = username+separator+"DATA" + " " +messageContent;
        sendMessageToServer(messageToSend);

        chatEditText.setText("");

        messages.add(messageContent);
        arrayAdapter.notifyDataSetChanged();


    }

    public void sendMessageToServer(String message) {


        TextSendingTask textSendingTask = new TextSendingTask();
        textSendingTask.execute(message);
        Log.i("Info", "We should send a message to the server");

    }

    public class TextSendingTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... strings) {

            try {


                Log.i("Info", "We are trying to send a message to the server");

                Log.i("Info", "Message sent to server: " + strings[0]);

                objectOutputStream.writeObject(strings[0]);
                objectOutputStream.reset();
                //objectOutputStream.writeUTF(strings[0]);
                //dout.writeUTF(strings[0]);



            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void processFinish(String output) {
        // Here we receive the result fired from async class
        // of onPostExecute(result) method.

        if (output != null) {
            Log.i("Info", "Server message: " + output);

            messages.add("You say:"+output);
            arrayAdapter.notifyDataSetChanged();
        }

    }

    public void drawGesture(View view){
        Intent gesture = new Intent(DataExchangingActivity.this,ColorChooser.class );
        startActivity(gesture);
    }

    /*
    public void getMessageFromServer () {


        ClientActivity.MessageGettingTask messageGettingTask = new ClientActivity().new MessageGettingTask();

        messageGettingTask.delegate = this;

        String output[] = new String[10];
        messageGettingTask.execute(output);
        // results will be in the output postexecute

    }
    */


}
