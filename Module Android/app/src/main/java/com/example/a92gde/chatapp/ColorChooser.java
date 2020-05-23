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

public class ColorChooser extends AppCompatActivity {
    private RadioButton red;
    private RadioButton green;
    private RadioButton blue;
    private Button validate;
    private RadioGroup group;
    private String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_color);

        red = (RadioButton) findViewById(R.id.radioButton);
        blue = (RadioButton) findViewById(R.id.radioButton2);
        green = (RadioButton) findViewById(R.id.radioButton3);
        validate=(Button)findViewById(R.id.button);
        group = (RadioGroup)findViewById(R.id.radioGroup);
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
        Intent intent = new Intent(ColorChooser.this,DrawingGestureActivity.class );
        intent.putExtra("color", color);
        startActivity(intent);
    }


}
