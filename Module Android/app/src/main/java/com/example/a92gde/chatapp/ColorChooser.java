package com.example.a92gde.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
@Deprecated
public class ColorChooser extends AppCompatActivity {
    private RadioButton red;
    private RadioButton green;
    private RadioButton blue;
    private Button validate;
    private RadioGroup group;
    private String color;
    private TextView chooseColorText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_color);

        red = (RadioButton) findViewById(R.id.radioButton);
        blue = (RadioButton) findViewById(R.id.radioButton2);
        green = (RadioButton) findViewById(R.id.radioButton3);
        validate=(Button)findViewById(R.id.button);
        group = (RadioGroup)findViewById(R.id.radioGroup);
        chooseColorText = (TextView)findViewById(R.id.textView) ;
    }

    public void drawGesture(View v){
        int selectedRadioButtonID = group.getCheckedRadioButtonId();

        // If nothing is selected from Radio Group, then it return -1
        if (selectedRadioButtonID != -1) {

            RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
            color = selectedRadioButton.getText().toString();

        }
        else{
            System.out.println("Nothing selected from Radio Group.");
        }
        //get the items transparents
        red.setAlpha(0);
        blue.setAlpha(0);
        green.setAlpha(0);
        chooseColorText.setAlpha(0);
        validate.setAlpha(0);

        //start a new activity
        Intent intent = new Intent(ColorChooser.this,DrawingGestureActivity.class );
        intent.putExtra("color", color);
        startActivity(intent);
    }


}
