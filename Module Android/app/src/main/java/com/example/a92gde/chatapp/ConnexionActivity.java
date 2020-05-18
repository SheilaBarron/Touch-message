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

public class ConnexionActivity extends AppCompatActivity implements AsyncResponse {

    private Button connexionButton = null;
    private Button creationButton = null;
    private String user;
    private ClientActivity.MessageGettingTask messageGettingTask;

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

                // We call the login function which will execute the asynctask
                login(user);

                Intent chat = new Intent(ConnexionActivity.this, DataExchangingActivity.class);
                chat.putExtra("user", user);
                startActivity(chat);


                }

        });

    }


    @Override
    public void processFinish(String output){
        // Here we receive the result fired from async class
        // of onPostExecute(result) method.
        Log.i("Info", "server answer for login: " + output);

        if (messageGettingTask != null ) {
            // && messageGettingTask.getStatus() == AsyncTask.Status.FINISHED
            // stop = true;

            if (output != null) {

                Toast.makeText(getApplicationContext(), "connexion validated by server: "+output, Toast.LENGTH_LONG).show();
                if (output.equals("true")) {

                    Intent menu = new Intent(ConnexionActivity.this, DataExchangingActivity.class);
                    menu.putExtra("user", user);
                    startActivity(menu);

                } else {
                    // We stay on the same activity

                }
            } else {
                Toast.makeText(getApplicationContext(), "Waiting for server response ", Toast.LENGTH_LONG).show();

            }
        }

    }

    public void login(String username) {

        // We send the connexion data
        ClientActivity.TextSendingTask textSendingTask = new ClientActivity().new TextSendingTask();
        textSendingTask.execute("LOGINREQUEST;"+username);
        Log.i("Info", "Message sent to server (code): " + "LOGINREQUEST");

        // We receive the server's answer

        //ClientActivity.MessageGettingTask messageGettingTask = new ClientActivity().new MessageGettingTask();
        messageGettingTask = new ClientActivity().new MessageGettingTask();
        // The delegate allows us to get a return value
        messageGettingTask.delegate = this;
        String output[] = new String[10];
        messageGettingTask.execute(output);

    }

}
