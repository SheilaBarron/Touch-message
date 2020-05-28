package com.example.a92gde.chatapp;

import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;

import java.net.*;
import java.io.*;

class ChatClient implements Runnable {
    Socket soc;

    Button btnSend,btnClose;
    //String sendTo;
    String loginName;
    String answerMessage;
    Thread t=null;
    DataOutputStream dout;
    DataInputStream din;
    static final int SERVER_PORT = 8010;

    // TODO change this ip according to your computer's:
    private static final String SERVER_IP = "192.168.1.64"; // ju lille

    ChatClient(String loginName) throws Exception {
        //super(loginName);
        super();
        this.loginName=loginName;
        //sendTo = chatwith;

        //btnSend=new Button("Send");
        //btnClose=new Button("Close");

        soc = new Socket(SERVER_IP,SERVER_PORT);

        din = new DataInputStream(soc.getInputStream());
        dout = new DataOutputStream(soc.getOutputStream());

        t=new Thread(this);
        t.start();

        Log.i("Info", "Logged in: " + loginName);

    }

    public boolean action(String e, String message)  {

        if(e.equals("Send")) {

            try {

                dout.writeUTF(loginName + " "  + "DATA" + " " + message);
                //dout.writeUTF(sendTo + " "  + "DATA" + " " + tf.getText().toString());
                //answerMessage = " loginName + \" says:\"";
                // ta.append("\n" + loginName + " says:" + tf.getText().toString());
                //tf.setText("");

            } catch(Exception ex) {

                return false;
            }

        } else if(e.equals("Close")) {

            try {

                dout.writeUTF(loginName + " LOGOUT");
                System.exit(1);

            } catch(Exception ex) {

                return false;
            }

        }

        return true;
    }

    public String getAnswerMessage() {

        return this.answerMessage;

    }

    /*
    public static void main(String args[]) throws Exception {

        ChatClient Client1 = new ChatClient(args[0]);

    }*/

    public void run() {

        while(true) {
            try {

                answerMessage = din.readUTF();

                //ta.append( "\n" + din.readUTF());

            } catch(Exception ex) {

                ex.printStackTrace();
            }
        }
    }
}