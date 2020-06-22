package com.example.a92gde.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnexionActivity extends AppCompatActivity {//implements AsyncResponse {

    private Button connexionButton = null;
    private String user;
    //private ClientActivity.MessageGettingTask messageGettingTask;

   // private ClientActivity.LoginTask loginResponseTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        connexionButton = (Button) findViewById(R.id.login);
        connexionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // We get the login info
                EditText usrBox = (EditText) findViewById(R.id.Username);
                user = usrBox.getText().toString(); //gets you the contents of edit text

                // we create the chat client:
                /*
                try {
                    ChatClient chatClient = new ChatClient(user);
                    //chatClient.

                } catch (Exception e) {
                    e.printStackTrace();

                }
                */

                // We call the login function which will execute the asynctask
                //login(user);
                Intent chat = new Intent(ConnexionActivity.this, DataExchangingActivity.class);
                chat.putExtra("user", user);
                startActivity(chat);

                /*
                Intent chat = new Intent(ConnexionActivity.this, DataExchangingActivity.class);
                chat.putExtra("user", user);
                startActivity(chat);
                */
                }
        });
    }
}
