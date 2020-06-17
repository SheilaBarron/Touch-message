package com.example.a92gde.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

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

    private final Handler handler = new Handler();
    private boolean end = false;

    private static final int SERVER_PORT = 8010;
    // TODO : put your compute's IP@
    private static final String SERVER_IP = "10.30.220.126"; // mease
    // private static final String SERVER_IP = "192.168.1.64"; // ju lille
    //private static final String SERVER_IP = "46.193.3.148"; // dom wifi

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Socket socketChars;

    private static String separator = " ";
    private static String username;

    private ArrayList<String> messages = new ArrayList<String>();
    private ArrayList<String> chatMessages = new ArrayList<String>();
    private ListView chatListView;
    private ChatArrayAdapter arrayAdapter;
    private Gesture latestGesture;

    private Gesture g;
    private String color;

    private ImageView imageView ;
    private ImageView imageViewGesture ;
    private Bitmap bitmap ;
    private boolean side = false;
    /*---------------------------------Take a screen shot of the chat-----------------------------*/

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            username = intent.getStringExtra("user");
        }

        Log.i("Info", "The name of the user is: "+username);

        /*
        if(intent.hasExtra("gesture")){
            g = (Gesture) intent.getSerializableExtra("gesture");
        }*/

        chatListView = (ListView) findViewById(R.id.chatListView);
        arrayAdapter = new ChatArrayAdapter(this, android.R.layout.simple_list_item_1, chatMessages);
        chatListView.setAdapter(arrayAdapter);

        ChatTask chatTask = new ChatTask();
        chatTask.delegate = this;
        chatTask.execute();

        //bitmap = screenShot(getWindow().getDecorView().getRootView()) ;



    }

    

    @Override
    public void onBackPressed() {

        sendMessageToServer(username+separator+"LOGOUT");
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void replayGesture(View view) {
        if(latestGesture==null){
            System.out.println("no gesture");
        }else{
            showGestureUI(latestGesture);
        }
    }

    /*-------------------------------------TASKS/SERVER-------------------------------------------*/
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

                InputStream inputStream = socketChars.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);


                if(objectInputStream == null || objectOutputStream == null) {

                    Log.i("Info", "NULL stream");
                }

                sendMessageToServer(username);

                startServerSocket();


            } catch (IOException  e) { //| ClassNotFoundException e
                e.printStackTrace();
            }

            return null;
        }



        protected void onPostExecute(String result) {

            delegate.processFinish(result);

        }


    }

    private void startServerSocket() {


        Thread thread = new Thread(new Runnable() {

            private String stringData = null;

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {

                Log.i("Info", "We started the server connexion");

                try {

                    while (!end) {

                        Object receivedObj = objectInputStream.readObject();
                        stringData = receivedObj.toString();

                        if (stringData != null) {

                            Gesture receivedGest = new Gesture();
                            if (receivedObj.getClass().equals(receivedGest.getClass())) { // gesture

                                receivedGest = new Gesture((Gesture)receivedObj); // we have the gesture
                                latestGesture = receivedGest;
                                showGestureUI(receivedGest);

                                //Intent intent_test = new Intent(DataExchangingActivity.this,ShowingGestureActivity.class );
                                //intent_test.putExtra("gesture", receivedGest);
                                //startActivity(intent_test);

                            } else { // text

                                updateUI(stringData);

                            }

                            Log.i("Info", "FROM SERVER - " + stringData);
                        }


                    }


                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            });

        thread.start();

    }


    /*----------------------------------SEND DATA/GESTURE-----------------------------------------*/
    // Send text button pressed
    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.chatEditText);
        final String messageContent = chatEditText.getText().toString();

        String messageToSend = username+separator+"DATA" + " " +messageContent;
        sendMessageToServer(messageToSend);

        chatEditText.setText("");
        messages.add("You say: "+messageContent);
        arrayAdapter.add(new ChatMessage(side, messageContent));
        arrayAdapter.notifyDataSetChanged();
    }


    public void sendMessageToServer(String message) {


        TextSendingTask textSendingTask = new TextSendingTask();
        textSendingTask.execute(message);

        /*
        if (g != null && !g.isEmpty()) {

            GestureSendingTask gestureSendingTask = new GestureSendingTask();
            gestureSendingTask.execute(g);
        }
        */

    }

    public class TextSendingTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            try {

                Log.i("Info", "Message we try to send to server: " + strings[0]);

                if(objectOutputStream!=null) {
                    objectOutputStream.reset();
                    objectOutputStream.writeObject(strings[0]);
                    objectOutputStream.reset();
                }


            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    public class GestureSendingTask extends AsyncTask<Gesture, Void, Void> {

        @Override
        protected Void doInBackground(Gesture... gestures) {

            try {

                Log.i("Info", "Message we try to send to server: " + gestures[0]);

                if (objectOutputStream!= null)
                objectOutputStream.writeObject(gestures[0]);
                //objectOutputStream.reset();


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

            messages.add("You say:" + output);
            arrayAdapter.notifyDataSetChanged();
        }

    }

    // update the UI after receiving a message
    private void updateUI(final String stringData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (stringData.trim().length() != 0){
                    messages.add(stringData);
                    if(arrayAdapter != null)
                        side=true;
                        arrayAdapter.add(new ChatMessage(side, stringData));
                        side=false;
                        arrayAdapter.notifyDataSetChanged();
                }

            }
        });
    }



    /*---------------------------------SELECT COLOR OF GESTURE------------------------------------*/
    public void selectGestureColor(View view) {


        bitmap = screenShot(getWindow().getDecorView().getRootView()) ;

        setContentView(R.layout.activity_gesture_color);

        Long initial_time;

        RadioButton red = (RadioButton) findViewById(R.id.radioButton);
        RadioButton blue = (RadioButton) findViewById(R.id.radioButton2);
        RadioButton green = (RadioButton) findViewById(R.id.radioButton3);
        Button validate=(Button)findViewById(R.id.button);
        RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup);
        TextView chooseColorText = (TextView)findViewById(R.id.textView) ;

        //Set the backgrond imageView
        imageView = (ImageView) findViewById(R.id.imageView) ;
        imageView.setImageBitmap(bitmap);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedRadioButtonID = group.getCheckedRadioButtonId();

                // If nothing is selected from Radio Group, then it return -1
                if (selectedRadioButtonID != -1) {

                    RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
                    color = selectedRadioButton.getText().toString();

                    Log.i("Info", "Selected color: " + color);
                }
                else{
                    System.out.println("Nothing selected from the color Radio Group.");
                }

            }
        });

        Button button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                startDrawing();

            }
        });

    }

    /*---------------------------------------DRAW GESTURE-----------------------------------------*/
    private Long initial_time;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startDrawing()
    {

        setContentView(R.layout.activity_drawinggesture);

        imageViewGesture = (ImageView) findViewById(R.id.imageViewGesture) ;
        imageViewGesture.setImageBitmap(bitmap);


        int[] size;
        ImageView[][] imageViews;
        Gesture gesture;
        //String color;

        TableLayout layout = (TableLayout) findViewById(R.id.layout);
        imageViews = new ImageView[10][6];

        gesture = new Gesture();
        gesture.setColor(color); // we already have it

        gesture.setOwner_user((username));

        Log.i("Info", "The owner user of the gesture: " + gesture.getOwner_user());


        // The grid
        ImageView imageView11 = (ImageView) findViewById(R.id.image11);
        ImageView imageView12 = (ImageView) findViewById(R.id.image12);
        ImageView imageView13 = (ImageView) findViewById(R.id.image13);
        ImageView imageView14 = (ImageView) findViewById(R.id.image14);
        ImageView imageView15 = (ImageView) findViewById(R.id.image15);
        ImageView imageView16 = (ImageView) findViewById(R.id.image16);
        imageViews[0][0]=imageView11;
        imageViews[0][1]=imageView12;
        imageViews[0][2]=imageView13;
        imageViews[0][3]=imageView14;
        imageViews[0][4]=imageView15;
        imageViews[0][5]=imageView16;

        ImageView imageView21 = (ImageView) findViewById(R.id.image21);
        ImageView imageView22 = (ImageView) findViewById(R.id.image22);
        ImageView imageView23 = (ImageView) findViewById(R.id.image23);
        ImageView imageView24 = (ImageView) findViewById(R.id.image24);
        ImageView imageView25 = (ImageView) findViewById(R.id.image25);
        ImageView imageView26 = (ImageView) findViewById(R.id.image26);
        imageViews[1][0]=imageView21;
        imageViews[1][1]=imageView22;
        imageViews[1][2]=imageView23;
        imageViews[1][3]=imageView24;
        imageViews[1][4]=imageView25;
        imageViews[1][5]=imageView26;

        ImageView imageView31 = (ImageView) findViewById(R.id.image31);
        ImageView imageView32 = (ImageView) findViewById(R.id.image32);
        ImageView imageView33 = (ImageView) findViewById(R.id.image33);
        ImageView imageView34 = (ImageView) findViewById(R.id.image34);
        ImageView imageView35 = (ImageView) findViewById(R.id.image35);
        ImageView imageView36 = (ImageView) findViewById(R.id.image36);
        imageViews[2][0]=imageView31;
        imageViews[2][1]=imageView32;
        imageViews[2][2]=imageView33;
        imageViews[2][3]=imageView34;
        imageViews[2][4]=imageView35;
        imageViews[2][5]=imageView36;

        ImageView imageView41 = (ImageView) findViewById(R.id.image41);
        ImageView imageView42 = (ImageView) findViewById(R.id.image42);
        ImageView imageView43 = (ImageView) findViewById(R.id.image43);
        ImageView imageView44 = (ImageView) findViewById(R.id.image44);
        ImageView imageView45 = (ImageView) findViewById(R.id.image45);
        ImageView imageView46 = (ImageView) findViewById(R.id.image46);
        imageViews[3][0]=imageView41;
        imageViews[3][1]=imageView42;
        imageViews[3][2]=imageView43;
        imageViews[3][3]=imageView44;
        imageViews[3][4]=imageView45;
        imageViews[3][5]=imageView46;

        ImageView imageView51 = (ImageView) findViewById(R.id.image51);
        ImageView imageView52 = (ImageView) findViewById(R.id.image52);
        ImageView imageView53 = (ImageView) findViewById(R.id.image53);
        ImageView imageView54 = (ImageView) findViewById(R.id.image54);
        ImageView imageView55 = (ImageView) findViewById(R.id.image55);
        ImageView imageView56 = (ImageView) findViewById(R.id.image56);
        imageViews[4][0]=imageView51;
        imageViews[4][1]=imageView52;
        imageViews[4][2]=imageView53;
        imageViews[4][3]=imageView54;
        imageViews[4][4]=imageView55;
        imageViews[4][5]=imageView56;

        ImageView imageView61 = (ImageView) findViewById(R.id.image61);
        ImageView imageView62 = (ImageView) findViewById(R.id.image62);
        ImageView imageView63 = (ImageView) findViewById(R.id.image63);
        ImageView imageView64 = (ImageView) findViewById(R.id.image64);
        ImageView imageView65 = (ImageView) findViewById(R.id.image65);
        ImageView imageView66 = (ImageView) findViewById(R.id.image66);
        imageViews[5][0]=imageView61;
        imageViews[5][1]=imageView62;
        imageViews[5][2]=imageView63;
        imageViews[5][3]=imageView64;
        imageViews[5][4]=imageView65;
        imageViews[5][5]=imageView66;

        ImageView imageView71 = (ImageView) findViewById(R.id.image71);
        ImageView imageView72 = (ImageView) findViewById(R.id.image72);
        ImageView imageView73 = (ImageView) findViewById(R.id.image73);
        ImageView imageView74 = (ImageView) findViewById(R.id.image74);
        ImageView imageView75 = (ImageView) findViewById(R.id.image75);
        ImageView imageView76 = (ImageView) findViewById(R.id.image76);
        imageViews[6][0]=imageView71;
        imageViews[6][1]=imageView72;
        imageViews[6][2]=imageView73;
        imageViews[6][3]=imageView74;
        imageViews[6][4]=imageView75;
        imageViews[6][5]=imageView76;

        ImageView imageView81 = (ImageView) findViewById(R.id.image81);
        ImageView imageView82 = (ImageView) findViewById(R.id.image82);
        ImageView imageView83 = (ImageView) findViewById(R.id.image83);
        ImageView imageView84 = (ImageView) findViewById(R.id.image84);
        ImageView imageView85 = (ImageView) findViewById(R.id.image85);
        ImageView imageView86 = (ImageView) findViewById(R.id.image86);
        imageViews[7][0]=imageView81;
        imageViews[7][1]=imageView82;
        imageViews[7][2]=imageView83;
        imageViews[7][3]=imageView84;
        imageViews[7][4]=imageView85;
        imageViews[7][5]=imageView86;

        ImageView imageView91 = (ImageView) findViewById(R.id.image91);
        ImageView imageView92 = (ImageView) findViewById(R.id.image92);
        ImageView imageView93 = (ImageView) findViewById(R.id.image93);
        ImageView imageView94 = (ImageView) findViewById(R.id.image94);
        ImageView imageView95 = (ImageView) findViewById(R.id.image95);
        ImageView imageView96 = (ImageView) findViewById(R.id.image96);
        imageViews[8][0]=imageView91;
        imageViews[8][1]=imageView92;
        imageViews[8][2]=imageView93;
        imageViews[8][3]=imageView94;
        imageViews[8][4]=imageView95;
        imageViews[8][5]=imageView96;

        ImageView imageView101 = (ImageView) findViewById(R.id.image101);
        ImageView imageView102 = (ImageView) findViewById(R.id.image102);
        ImageView imageView103 = (ImageView) findViewById(R.id.image103);
        ImageView imageView104 = (ImageView) findViewById(R.id.image104);
        ImageView imageView105 = (ImageView) findViewById(R.id.image105);
        ImageView imageView106 = (ImageView) findViewById(R.id.image106);
        imageViews[9][0]=imageView101;
        imageViews[9][1]=imageView102;
        imageViews[9][2]=imageView103;
        imageViews[9][3]=imageView104;
        imageViews[9][4]=imageView105;
        imageViews[9][5]=imageView106;

        size=new int[2];

        layout.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();


                size[0]=coord(imageView33)[0]-coord(imageView22)[0];
                size[1]=coord(imageView33)[1]-coord(imageView22)[1];

                int x = (int) event.getX();
                int y = (int) event.getY();
                int column = (int)Math.floor(x/size[0])+1;
                int row = (int)Math.floor(y/size[1])+1;

                ImageView im = imageViews[row-1][ column-1];

                switch(event.getAction()) {

                    case MotionEvent.ACTION_UP:

                        // come back to chat room
                        g = gesture;

                        GestureSendingTask gestureSendingTask = new GestureSendingTask();
                        gestureSendingTask.execute(gesture);

                        layout.setVisibility(View.INVISIBLE);
                        setContentView(R.layout.activity_chat);
                        chatListView = (ListView) findViewById(R.id.chatListView);
                        arrayAdapter = new ChatArrayAdapter(DataExchangingActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
                        for (int i=0; i<messages.size(); i++){
                            String m = messages.get(i);
                            int position = m.indexOf("You say");
                            if (position ==-1){
                                arrayAdapter.add(new ChatMessage(true, m));
                            }else{
                                m= m.replace("You say: ", "");
                                arrayAdapter.add(new ChatMessage(false,  m));
                            }
                        }
                        chatListView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                        if (chatListView == null)
                        Log.i("Info", "Problems with the list view");

                        if (arrayAdapter == null)
                            Log.i("Info", "Problems with the array adapter ");

                        if (messages.isEmpty())
                            Log.i("Info", "Problems with the messages ");

                }


                for (int i=0; i<11; i++){
                    for (int j=0; j< 7; j++){
                        if((i!=row) | (j!=column)){
                            if (gesture.isIn(i,j)==true) {
                                ImageView b = imageViews[i - 1][j - 1];
                                if (color.equals("Red")) {
                                    b.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_rouge_pale));
                                } else {
                                    if (color.equals("Blue")) {
                                        b.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_bleu_pale));
                                    } else {
                                        b.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_vert_pale));
                                    }
                                }
                            }
                        }
                    }
                }

                if( gesture.isIn(row, column)==false) {
                    long[] mVibratePattern = new long[]{0, 100};
                    int[] mAmplitudes = new int[]{0, (row-1)*255/10};
                    System.out.println(mAmplitudes[1]);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    VibrationEffect effect = VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, -1);
                    v.vibrate(effect);
                    if (color.equals("Red")) {
                        im.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_rouge));
                    } else {
                        if (color.equals("Blue")) {
                            im.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_bleu));
                        } else {
                            im.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_vert));
                        }
                    }

                    if(gesture.isEmpty()==true){
                        initial_time=Long.parseLong(ts); // TODO
                        Box box = new Box(row, column, 0);
                        gesture.addBox(box);
                    }else{
                        //Box box = new Box(row, column, (int)(Long.parseLong(ts))); // TODO
                        Box box = new Box(row, column, (int)(Long.parseLong(ts)-initial_time));
                        System.out.println((int)(Long.parseLong(ts)-initial_time)); // TODO
                        gesture.addBox(box);
                    }


                }

                return true;
            }
        });

    }

    public int[] coord(ImageView im){
        int[] coordinates = new int[2];
        im.getLocationOnScreen(coordinates);
        return coordinates;
    }

    /*--------------------------------------SHOW/DISPLAY GESTURE----------------------------------*/
    private boolean stop = false;
    private Runnable r = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showGestureUI(Gesture receivedGest) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.activity_showinggesture);

                ImageView[][] imageViews;
                String color = receivedGest.getColor();
                Gesture gesture = receivedGest;
                int interval;
                Handler handler;
                //boolean stop = false; // outside
                //Runnable r = null; // outside

                TableLayout layout = (TableLayout) findViewById(R.id.layout);
                imageViews = new ImageView[10][6];

                ImageView imageView11 = (ImageView) findViewById(R.id.image11);
                ImageView imageView12 = (ImageView) findViewById(R.id.image12);
                ImageView imageView13 = (ImageView) findViewById(R.id.image13);
                ImageView imageView14 = (ImageView) findViewById(R.id.image14);
                ImageView imageView15 = (ImageView) findViewById(R.id.image15);
                ImageView imageView16 = (ImageView) findViewById(R.id.image16);
                imageViews[0][0]=imageView11;
                imageViews[0][1]=imageView12;
                imageViews[0][2]=imageView13;
                imageViews[0][3]=imageView14;
                imageViews[0][4]=imageView15;
                imageViews[0][5]=imageView16;

                ImageView imageView21 = (ImageView) findViewById(R.id.image21);
                ImageView imageView22 = (ImageView) findViewById(R.id.image22);
                ImageView imageView23 = (ImageView) findViewById(R.id.image23);
                ImageView imageView24 = (ImageView) findViewById(R.id.image24);
                ImageView imageView25 = (ImageView) findViewById(R.id.image25);
                ImageView imageView26 = (ImageView) findViewById(R.id.image26);
                imageViews[1][0]=imageView21;
                imageViews[1][1]=imageView22;
                imageViews[1][2]=imageView23;
                imageViews[1][3]=imageView24;
                imageViews[1][4]=imageView25;
                imageViews[1][5]=imageView26;

                ImageView imageView31 = (ImageView) findViewById(R.id.image31);
                ImageView imageView32 = (ImageView) findViewById(R.id.image32);
                ImageView imageView33 = (ImageView) findViewById(R.id.image33);
                ImageView imageView34 = (ImageView) findViewById(R.id.image34);
                ImageView imageView35 = (ImageView) findViewById(R.id.image35);
                ImageView imageView36 = (ImageView) findViewById(R.id.image36);
                imageViews[2][0]=imageView31;
                imageViews[2][1]=imageView32;
                imageViews[2][2]=imageView33;
                imageViews[2][3]=imageView34;
                imageViews[2][4]=imageView35;
                imageViews[2][5]=imageView36;

                ImageView imageView41 = (ImageView) findViewById(R.id.image41);
                ImageView imageView42 = (ImageView) findViewById(R.id.image42);
                ImageView imageView43 = (ImageView) findViewById(R.id.image43);
                ImageView imageView44 = (ImageView) findViewById(R.id.image44);
                ImageView imageView45 = (ImageView) findViewById(R.id.image45);
                ImageView imageView46 = (ImageView) findViewById(R.id.image46);
                imageViews[3][0]=imageView41;
                imageViews[3][1]=imageView42;
                imageViews[3][2]=imageView43;
                imageViews[3][3]=imageView44;
                imageViews[3][4]=imageView45;
                imageViews[3][5]=imageView46;

                ImageView imageView51 = (ImageView) findViewById(R.id.image51);
                ImageView imageView52 = (ImageView) findViewById(R.id.image52);
                ImageView imageView53 = (ImageView) findViewById(R.id.image53);
                ImageView imageView54 = (ImageView) findViewById(R.id.image54);
                ImageView imageView55 = (ImageView) findViewById(R.id.image55);
                ImageView imageView56 = (ImageView) findViewById(R.id.image56);
                imageViews[4][0]=imageView51;
                imageViews[4][1]=imageView52;
                imageViews[4][2]=imageView53;
                imageViews[4][3]=imageView54;
                imageViews[4][4]=imageView55;
                imageViews[4][5]=imageView56;

                ImageView imageView61 = (ImageView) findViewById(R.id.image61);
                ImageView imageView62 = (ImageView) findViewById(R.id.image62);
                ImageView imageView63 = (ImageView) findViewById(R.id.image63);
                ImageView imageView64 = (ImageView) findViewById(R.id.image64);
                ImageView imageView65 = (ImageView) findViewById(R.id.image65);
                ImageView imageView66 = (ImageView) findViewById(R.id.image66);
                imageViews[5][0]=imageView61;
                imageViews[5][1]=imageView62;
                imageViews[5][2]=imageView63;
                imageViews[5][3]=imageView64;
                imageViews[5][4]=imageView65;
                imageViews[5][5]=imageView66;

                ImageView imageView71 = (ImageView) findViewById(R.id.image71);
                ImageView imageView72 = (ImageView) findViewById(R.id.image72);
                ImageView imageView73 = (ImageView) findViewById(R.id.image73);
                ImageView imageView74 = (ImageView) findViewById(R.id.image74);
                ImageView imageView75 = (ImageView) findViewById(R.id.image75);
                ImageView imageView76 = (ImageView) findViewById(R.id.image76);
                imageViews[6][0]=imageView71;
                imageViews[6][1]=imageView72;
                imageViews[6][2]=imageView73;
                imageViews[6][3]=imageView74;
                imageViews[6][4]=imageView75;
                imageViews[6][5]=imageView76;

                ImageView imageView81 = (ImageView) findViewById(R.id.image81);
                ImageView imageView82 = (ImageView) findViewById(R.id.image82);
                ImageView imageView83 = (ImageView) findViewById(R.id.image83);
                ImageView imageView84 = (ImageView) findViewById(R.id.image84);
                ImageView imageView85 = (ImageView) findViewById(R.id.image85);
                ImageView imageView86 = (ImageView) findViewById(R.id.image86);
                imageViews[7][0]=imageView81;
                imageViews[7][1]=imageView82;
                imageViews[7][2]=imageView83;
                imageViews[7][3]=imageView84;
                imageViews[7][4]=imageView85;
                imageViews[7][5]=imageView86;

                ImageView imageView91 = (ImageView) findViewById(R.id.image91);
                ImageView imageView92 = (ImageView) findViewById(R.id.image92);
                ImageView imageView93 = (ImageView) findViewById(R.id.image93);
                ImageView imageView94 = (ImageView) findViewById(R.id.image94);
                ImageView imageView95 = (ImageView) findViewById(R.id.image95);
                ImageView imageView96 = (ImageView) findViewById(R.id.image96);
                imageViews[8][0]=imageView91;
                imageViews[8][1]=imageView92;
                imageViews[8][2]=imageView93;
                imageViews[8][3]=imageView94;
                imageViews[8][4]=imageView95;
                imageViews[8][5]=imageView96;

                ImageView imageView101 = (ImageView) findViewById(R.id.image101);
                ImageView imageView102 = (ImageView) findViewById(R.id.image102);
                ImageView imageView103 = (ImageView) findViewById(R.id.image103);
                ImageView imageView104 = (ImageView) findViewById(R.id.image104);
                ImageView imageView105 = (ImageView) findViewById(R.id.image105);
                ImageView imageView106 = (ImageView) findViewById(R.id.image106);
                imageViews[9][0]=imageView101;
                imageViews[9][1]=imageView102;
                imageViews[9][2]=imageView103;
                imageViews[9][3]=imageView104;
                imageViews[9][4]=imageView105;
                imageViews[9][5]=imageView106;

                Box lastBox = gesture.getBoxes().get(gesture.getNumberOfBoxes()-1);
                Box firstBox = gesture.getBoxes().get(0);
                interval = 1;

                Log.i("Info", "Starting showing gesture to user: " + username);

                handler = new Handler();

                r = new Runnable() {

                    int currentIndex = 0;
                    int time = 0;
                    String color = gesture.getColor();
                    ArrayList<Box> boxes = gesture.getBoxes();

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {

                        if (stop==false) {
                            Box currentBox = boxes.get(currentIndex);
                            int timestamp = currentBox.getTime();
                            if (time == timestamp) {
                                int row = currentBox.getRow();
                                int column = currentBox.getColumn();
                                long[] mVibratePattern = new long[]{0, 100};
                                int[] mAmplitudes = new int[]{0, (row-1)*255/10};
                                System.out.println(mAmplitudes[1]);
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                VibrationEffect effect = VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, -1);
                                v.vibrate(effect);

                                ImageView im = imageViews[row - 1][column - 1];
                                if (color.equals("Red")) {
                                    im.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_rouge));
                                } else {
                                    if (color.equals("Green")) {
                                        im.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_vert));
                                    } else {
                                        im.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_bleu));
                                    }
                                }

                                if (currentIndex != 0) {
                                    for (int i = 0; i < currentIndex; i++) {
                                        Box previousBox = boxes.get(i);
                                        int rowPreviousBox = previousBox.getRow();
                                        int columnPreviousBox = previousBox.getColumn();

                                        ImageView imPreviousBox = imageViews[rowPreviousBox - 1][columnPreviousBox - 1];
                                        if (color.equals("Red")) {
                                            imPreviousBox.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_rouge_pale));
                                        } else {
                                            if (color.equals("Green")) {
                                                imPreviousBox.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_vert_pale));
                                            } else {
                                                imPreviousBox.setImageDrawable(ContextCompat.getDrawable(DataExchangingActivity.this, R.drawable.carre_bleu_pale));
                                            }
                                        }
                                    }
                                }

                                if (currentIndex == gesture.getNumberOfBoxes() - 1) {
                                    stop = true;
                                }
                                System.out.println(currentIndex);
                                currentIndex += 1;
                            }

                            else{time += 1;}
                            handler.postDelayed(r, interval);
                        } else {
                            // Intent message = new Intent(DataExchangingActivity.this, DataExchangingActivity.class);
                            // startActivity(message);
                            handler.removeCallbacks(r);

                            layout.setVisibility(View.INVISIBLE);
                            setContentView(R.layout.activity_chat);
                            chatListView = (ListView) findViewById(R.id.chatListView);
                            arrayAdapter = new ChatArrayAdapter(DataExchangingActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
                            for (int i=0; i<messages.size(); i++){
                                String m = messages.get(i);
                                int position = m.indexOf("You say");
                                if (position ==-1){
                                    arrayAdapter.add(new ChatMessage(true, m));
                                }else{
                                    m= m.replace("You say: ", "");
                                    arrayAdapter.add(new ChatMessage(false,  m));
                                }
                            }
                            chatListView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();

                            if (chatListView == null)
                                Log.i("Info", "Problems with the list view");

                            if (arrayAdapter == null)
                                Log.i("Info", "Problems with the array adapter");

                            if (messages.isEmpty())
                                Log.i("Info", "Problems with the messages");

                        }

                    }
                };

                //runOnUiThread((r));
                stop = false;
                r.run();


            }
        });

    }

    /*----------------------------------------DEPRECATED------------------------------------------*/

    // Worked with the showGesture button which is no longer used
    public void show(View view) {

        Intent intent_test = new Intent(DataExchangingActivity.this,ShowingGestureActivity.class );
        intent_test.putExtra("gesture", g);
        startActivity(intent_test);

    }


}
