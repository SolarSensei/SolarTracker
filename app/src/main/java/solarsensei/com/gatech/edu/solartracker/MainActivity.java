package solarsensei.com.gatech.edu.solartracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by timothybaba on 8/7/17.
 */

public class MainActivity extends AppCompatActivity {

    private Button manualButton;
    private Button autoButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manualButton = (Button) findViewById(R.id.manual);
        autoButton = (Button) findViewById(R.id.auto);

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manualActivity = new Intent(getApplicationContext(), ManualActivity.class);
                startActivity(manualActivity);
                finish();
            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sensorActivity = new Intent(getApplicationContext(), SensorActivity.class);
                startActivity(sensorActivity);
                finish();
            }
        });
    }
}
