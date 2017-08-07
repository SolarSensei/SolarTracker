package solarsensei.com.gatech.edu.solartracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import static android.R.attr.button;

/**
 * Created by timothybaba on 8/7/17.
 */

public class ManualActivity extends AppCompatActivity {

    private Button rightButton;
    private Button leftButton;
    private Button upButton;
    private Button downButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        rightButton = (Button) findViewById(R.id.right);
        leftButton  =  (Button) findViewById(R.id.left);
        upButton  = (Button) findViewById(R.id.up);
        downButton = (Button) findViewById(R.id.down);

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //send turn right data;
                }
                return false;
            }
        });

        leftButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //send data
                }
                return  false;
            }
        });

        upButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                   //send data
                }
                return false;
            }
        });

        downButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                   //send data;
                }
                return false;
            }
        });
    }
}
