package solarsensei.com.gatech.edu.solartracker.controllers;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import solarsensei.com.gatech.edu.solartracker.R;

/**
 * Created by timothybaba on 8/7/17.
 */

public class MainActivity extends AppCompatActivity {

    private boolean bt_discoverable = false;
    private final static int  REQUEST_DISCOVER_BT = 1;

    public static  BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Button manualButton = (Button) findViewById(R.id.manual);
        Button autoButton = (Button) findViewById(R.id.auto);


        checkBluetoothStatus();

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_discoverable) {
                    Intent manualActivity = new Intent(getApplicationContext(), ManualActivity.class);
                    startActivity(manualActivity);
                }

            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_discoverable) {
                    Intent sensorActivity = new Intent(getApplicationContext(), SensorActivity.class);
                    startActivity(sensorActivity);
                }

            }
        });
    }

    private void checkBluetoothStatus() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getBaseContext(), "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        } else {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DISCOVER_BT) {
            if (resultCode != 0) {
                //bluetooth discoverable
               bt_discoverable = true;

            } else {
                Toast.makeText(getBaseContext(), "You must enable bluetooth to transfer data", Toast.LENGTH_SHORT).show();
            }
        }


    }

}
