package com.example.a92gde.chatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActivity {


    private static Socket socketChars;
    private Socket socketImage;

    // private static InputStreamReader isr;
    private static BufferedReader br;
    private static PrintWriter pw;

    private static final int SERVER_PORT = 8010;

    String loginName;
    DataOutputStream dout;
    DataInputStream din;

    // private static final String SERVER_IP = "10.30.216.98"; // mease
    // private static final String SERVER_IP = "137.194.95.157"; // telecom sheila

    // TODO change this ip according to your computer's:
    private static final String SERVER_IP = "192.168.1.64"; // ju lille

    // The method used when we click on Send
    public boolean sendMessage(String message) {

        // Send the text
        TextSendingTask m = new TextSendingTask();

        if (m.execute() == null) {
            Log.i("Info", "Message sent to server : " + message);
            return true;

        } else {

            return false;
        }

    }


    public class LoginTask extends AsyncTask<String, Void, String> {

        // In order to get a return message
        public AsyncResponse delegate = null;

        @Override
        protected String doInBackground(String... params) {

            String serverMessage = "";

                try {

                    Log.i("Info", "We are trying to connect to the server");
                    socketChars = new Socket(SERVER_IP, SERVER_PORT); // Connect to the socket

                    Log.i("Info", "Message sent to server: " + params[0]);
                    dout = new DataOutputStream(socketChars.getOutputStream());
                    dout.writeUTF(params[0]);

                    Log.i("Info", "We are trying to get a response from the server");

                    din = new DataInputStream(socketChars.getInputStream());
                    serverMessage = din.readUTF();

                    Log.i("Info", "Message received by server: " + serverMessage);

                    params[1] = serverMessage;


                } catch (IOException e) {

                    Log.i("Info", "Error here when reading");
                    e.printStackTrace();
                }

            return serverMessage;

        }

        protected void onPostExecute(String result) {

            delegate.processFinish(result);

        }

    }

    public class ChatTask extends AsyncTask<String, Void, String> {

        // In order to get a return message
        public AsyncResponse delegate = null;

        @Override
        protected String doInBackground(String... params) {

            String serverMessage = "";

            try {

                Log.i("Info", "We are trying to connect to the server");
                socketChars = new Socket(SERVER_IP, SERVER_PORT); // Connect to the socket

                Log.i("Info", "Message sent to server: " + params[0]);
                dout = new DataOutputStream(socketChars.getOutputStream());
                dout.writeUTF(params[0]);

                Log.i("Info", "We are trying to get a response from the server");

                while(!this.isCancelled()) {

                    din = new DataInputStream(socketChars.getInputStream());
                    serverMessage = din.readUTF();

                    Log.i("Info", "Message received by server: " + serverMessage);

                    params[1] = serverMessage;
                }


            } catch (IOException e) {

                Log.i("Info", "Error here when reading");
                e.printStackTrace();
            }

            return serverMessage;

        }


        protected void onPause(){
            this.cancel(false);
        }

        protected void onPostExecute(String result) {

            delegate.processFinish(result);

        }

    }

    public class TextSendingTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... strings) {

            try {


                Log.i("Info", "We are trying to connect to the server");
                socketChars = new Socket(SERVER_IP, SERVER_PORT); // Connect to the socket

                din = new DataInputStream(socketChars.getInputStream());

                Log.i("Info", "Message sent to server: " + strings[0]);
                dout = new DataOutputStream(socketChars.getOutputStream());
                dout.writeUTF(strings[0]);


                /*
                Log.i("Info", "We are trying to send a message to the server");
                socketChars = new Socket(SERVER_IP, SERVER_PORT); // Connect to the socket

                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter((
                        socketChars.getOutputStream())))); // Set the output steam
                Log.i("Info", "Message sent to server: " + strings[0]);
                pw.println(strings[0]);  // Send the message through the socket

                pw.flush();
                pw.close();
                socketChars.close();
                */



            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    public class MessageGettingTask extends AsyncTask<String, Void, String> {

        // In order to get a return message
        public AsyncResponse delegate = null;

        // private String messageReceived;

        @Override
        protected String doInBackground(String... params) {

            String serverMessage = "";

            while (true) {

                try {


                    Log.i("Info", "We are trying to connect to the server");
                    socketChars = new Socket(SERVER_IP, SERVER_PORT); // Connect to the socket

                    din = new DataInputStream(socketChars.getInputStream());

                    serverMessage = din.readUTF();

                    Log.i("Info", "Message received by server: " + serverMessage);

                    params[0] = serverMessage;




                /*
                socketChars = new Socket(SERVER_IP, SERVER_PORT); // Connect to the socket

                br = new BufferedReader(new BufferedReader(new InputStreamReader((
                        socketChars.getInputStream()))));

                serverMessage = br.readLine(); // we read the message sent by the server
                Log.i("Fractale", "Message received by server: " + serverMessage);

                params[0] = serverMessage;
                br.close();
                socketChars.close();
                */

                    //return serverMessage;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //return serverMessage;

        }

        protected void onPostExecute(String result) {

            delegate.processFinish(result);


        }

    }

}