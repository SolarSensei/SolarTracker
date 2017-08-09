package solarsensei.com.gatech.edu.solartracker.controllers;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import solarsensei.com.gatech.edu.solartracker.R;

import static android.R.attr.button;

/**
 * Created by timothybaba on 8/7/17.
 */

public class ManualActivity extends AppCompatActivity {

    private Button rightButton;
    private Button leftButton;
    private Button upButton;
    private Button downButton;
    private Drawable buttonBackground;
    int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        rightButton = (Button) findViewById(R.id.right);
        leftButton  =  (Button) findViewById(R.id.left);
        upButton  = (Button) findViewById(R.id.up);
        downButton = (Button) findViewById(R.id.down);

       buttonBackground = rightButton.getBackground();

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightButton.setBackgroundColor(Color.GREEN);
                    //send turn right data;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    rightButton.setBackground(buttonBackground);

                }

                return false;
            }
        });

        leftButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftButton.setBackgroundColor(Color.GREEN);
                    //send data
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftButton.setBackground(buttonBackground);
                }
                return  false;
            }
        });

        upButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    upButton.setBackgroundColor(Color.GREEN);
                    //send data
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    upButton.setBackground(buttonBackground);
                }
                return  false;
            }
        });

        downButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    downButton.setBackgroundColor(Color.GREEN);
                    //send data
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    downButton.setBackground(buttonBackground);
                }
                return  false;
            }
        });
    }
}
