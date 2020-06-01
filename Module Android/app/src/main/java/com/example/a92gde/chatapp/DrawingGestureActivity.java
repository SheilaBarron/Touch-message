package com.example.a92gde.chatapp;

import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toolbar;

import java.util.ArrayList;

public class DrawingGestureActivity extends AppCompatActivity {

    private int[] size;
    private ImageView[][] imageViews;
    private Gesture gesture;
    private int initial_time;
    private String color;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawinggesture);

        TableLayout layout = (TableLayout) findViewById(R.id.layout);
        imageViews = new ImageView[9][5];

        Intent intent = getIntent();
        if (intent.hasExtra("color")){ // vérifie qu'une valeur est associée à la clé “edittext”
            color = intent.getStringExtra("color"); // on récupère la valeur associée à la clé
        }

        System.out.println(color);
        gesture = new Gesture();
        gesture.setColor(color);


        ImageView imageView11 = (ImageView) findViewById(R.id.image11);
        ImageView imageView12 = (ImageView) findViewById(R.id.image12);
        ImageView imageView13 = (ImageView) findViewById(R.id.image13);
        ImageView imageView14 = (ImageView) findViewById(R.id.image14);
        ImageView imageView15 = (ImageView) findViewById(R.id.image15);
        imageViews[0][0]=imageView11;
        imageViews[0][1]=imageView12;
        imageViews[0][2]=imageView13;
        imageViews[0][3]=imageView14;
        imageViews[0][4]=imageView15;

        ImageView imageView21 = (ImageView) findViewById(R.id.image21);
        ImageView imageView22 = (ImageView) findViewById(R.id.image22);
        ImageView imageView23 = (ImageView) findViewById(R.id.image23);
        ImageView imageView24 = (ImageView) findViewById(R.id.image24);
        ImageView imageView25 = (ImageView) findViewById(R.id.image25);
        imageViews[1][0]=imageView21;
        imageViews[1][1]=imageView22;
        imageViews[1][2]=imageView23;
        imageViews[1][3]=imageView24;
        imageViews[1][4]=imageView25;

        ImageView imageView31 = (ImageView) findViewById(R.id.image31);
        ImageView imageView32 = (ImageView) findViewById(R.id.image32);
        ImageView imageView33 = (ImageView) findViewById(R.id.image33);
        ImageView imageView34 = (ImageView) findViewById(R.id.image34);
        ImageView imageView35 = (ImageView) findViewById(R.id.image35);
        imageViews[2][0]=imageView31;
        imageViews[2][1]=imageView32;
        imageViews[2][2]=imageView33;
        imageViews[2][3]=imageView34;
        imageViews[2][4]=imageView35;

        ImageView imageView41 = (ImageView) findViewById(R.id.image41);
        ImageView imageView42 = (ImageView) findViewById(R.id.image42);
        ImageView imageView43 = (ImageView) findViewById(R.id.image43);
        ImageView imageView44 = (ImageView) findViewById(R.id.image44);
        ImageView imageView45 = (ImageView) findViewById(R.id.image45);
        imageViews[3][0]=imageView41;
        imageViews[3][1]=imageView42;
        imageViews[3][2]=imageView43;
        imageViews[3][3]=imageView44;
        imageViews[3][4]=imageView45;

        ImageView imageView51 = (ImageView) findViewById(R.id.image51);
        ImageView imageView52 = (ImageView) findViewById(R.id.image52);
        ImageView imageView53 = (ImageView) findViewById(R.id.image53);
        ImageView imageView54 = (ImageView) findViewById(R.id.image54);
        ImageView imageView55 = (ImageView) findViewById(R.id.image55);
        imageViews[4][0]=imageView51;
        imageViews[4][1]=imageView52;
        imageViews[4][2]=imageView53;
        imageViews[4][3]=imageView54;
        imageViews[4][4]=imageView55;

        ImageView imageView61 = (ImageView) findViewById(R.id.image61);
        ImageView imageView62 = (ImageView) findViewById(R.id.image62);
        ImageView imageView63 = (ImageView) findViewById(R.id.image63);
        ImageView imageView64 = (ImageView) findViewById(R.id.image64);
        ImageView imageView65 = (ImageView) findViewById(R.id.image65);
        imageViews[5][0]=imageView61;
        imageViews[5][1]=imageView62;
        imageViews[5][2]=imageView63;
        imageViews[5][3]=imageView64;
        imageViews[5][4]=imageView65;

        ImageView imageView71 = (ImageView) findViewById(R.id.image71);
        ImageView imageView72 = (ImageView) findViewById(R.id.image72);
        ImageView imageView73 = (ImageView) findViewById(R.id.image73);
        ImageView imageView74 = (ImageView) findViewById(R.id.image74);
        ImageView imageView75 = (ImageView) findViewById(R.id.image75);
        imageViews[6][0]=imageView71;
        imageViews[6][1]=imageView72;
        imageViews[6][2]=imageView73;
        imageViews[6][3]=imageView74;
        imageViews[6][4]=imageView75;

        ImageView imageView81 = (ImageView) findViewById(R.id.image81);
        ImageView imageView82 = (ImageView) findViewById(R.id.image82);
        ImageView imageView83 = (ImageView) findViewById(R.id.image83);
        ImageView imageView84 = (ImageView) findViewById(R.id.image84);
        ImageView imageView85 = (ImageView) findViewById(R.id.image85);
        imageViews[7][0]=imageView81;
        imageViews[7][1]=imageView82;
        imageViews[7][2]=imageView83;
        imageViews[7][3]=imageView84;
        imageViews[7][4]=imageView85;

        ImageView imageView91 = (ImageView) findViewById(R.id.image91);
        ImageView imageView92 = (ImageView) findViewById(R.id.image92);
        ImageView imageView93 = (ImageView) findViewById(R.id.image93);
        ImageView imageView94 = (ImageView) findViewById(R.id.image94);
        ImageView imageView95 = (ImageView) findViewById(R.id.image95);
        imageViews[8][0]=imageView91;
        imageViews[8][1]=imageView92;
        imageViews[8][2]=imageView93;
        imageViews[8][3]=imageView94;
        imageViews[8][4]=imageView95;

        size=new int[2];


        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();


                size[0]=coord(imageView33)[0]-coord(imageView22)[0];
                size[1]=coord(imageView33)[1]-coord(imageView22)[1];

                int x = (int) event.getX();
                int y = (int) event.getY();
                int column = (int)Math.floor(x/size[0])+1;
                int row = (int)Math.floor(y/size[1])+1;

                ImageView im = imageViews[row-1][ column-1];


                for (int i=0; i<10; i++){
                    for (int j=0; j< 6; j++){
                        if((i!=row) | (j!=column)){
                        if (gesture.isIn(i,j)==true) {
                            ImageView b = imageViews[i - 1][j - 1];
                            if (color.equals("Red")) {
                                b.setImageDrawable(ContextCompat.getDrawable(DrawingGestureActivity.this, R.drawable.carre_rouge_pale));
                            } else {
                                if (color.equals("Blue")) {
                                    b.setImageDrawable(ContextCompat.getDrawable(DrawingGestureActivity.this, R.drawable.carre_bleu_pale));
                                } else {
                                    b.setImageDrawable(ContextCompat.getDrawable(DrawingGestureActivity.this, R.drawable.carre_vert_pale));
                                }
                            }
                        }
                        }
                    }
                }

                if( gesture.isIn(row, column)==false) {
                    if (color.equals("Red")) {
                        im.setImageDrawable(ContextCompat.getDrawable(DrawingGestureActivity.this, R.drawable.carre_rouge));
                    } else {
                        if (color.equals("Blue")) {
                            im.setImageDrawable(ContextCompat.getDrawable(DrawingGestureActivity.this, R.drawable.carre_bleu));
                        } else {
                            im.setImageDrawable(ContextCompat.getDrawable(DrawingGestureActivity.this, R.drawable.carre_vert));
                        }
                    }

                    if(gesture.isEmpty()==true){
                        initial_time=Integer.parseInt(ts);
                        Box box = new Box(row, column, 0);
                        gesture.addBox(box);
                    }else{
                        Box box = new Box(row, column, Integer.parseInt(ts)-initial_time);
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
}
