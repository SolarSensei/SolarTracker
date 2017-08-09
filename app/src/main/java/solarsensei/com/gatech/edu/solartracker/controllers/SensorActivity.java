package solarsensei.com.gatech.edu.solartracker.controllers;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import solarsensei.com.gatech.edu.solartracker.R;
import solarsensei.com.gatech.edu.solartracker.model.ConnectThread;
import solarsensei.com.gatech.edu.solartracker.model.ConnectedThread;

/**
 * Created by timothybaba on 5/19/17.
 */
public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    /**
     * widgets
     */
    private Button mButton;
    private TextView mPressureView;
    private TextView mTempView;
    private TextView mLightView;
    private TextView mHumidityView;
    private TextView mMagneticView;
    private TextView azimuthView;
    private TextView pitchView;
    private TextView rollView;
    private float[] mRotationMatrix = new float[9];
    private float[] mOrientationValues = new float[3];
    private TextView msg;

    public static AlertDialog dialog;
    public static ProgressDialog mProgressDialog;
    private SensorManager mSensorManager;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;



    //Environmental sensors
    private Sensor mPressure;
    private Sensor mTemperature;
    private Sensor mLight;
    private Sensor mRelativeHumidity;
    private Sensor mMagneticField;

    //motion sensors
    private Sensor mRotation;

    //constants
    private final String startTransfer = "START DATA TRANSFER";
    private final String stopTransfer = "STOP DATA TRANSFER";


    //Standard SerialPortService ID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Bluetooth gadgets
    private BluetoothSocket btSocket = null;
    private ConnectedThread mConnectedThread;
    private String deviceAddress;
    private BluetoothAdapter mBtAdapter = MainActivity.mBtAdapter;

    //public static Context activity;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mPressureView = (TextView) findViewById(R.id.pressureReading);
        mTempView = (TextView) findViewById(R.id.tempReading);
        mLightView = (TextView) findViewById(R.id.lightReading);
        mHumidityView = (TextView) findViewById(R.id.rHumidity);
        mMagneticView = (TextView) findViewById(R.id.magneticField);
        azimuthView = (TextView) findViewById(R.id.azimuth);
        pitchView = (TextView) findViewById(R.id.pitch);
        rollView = (TextView) findViewById(R.id.roll);
        mButton = (Button) findViewById(R.id.startPairing);

        mButton.setText(startTransfer);

        // Gets an instance of the sensor service, and uses that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mRelativeHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

       // activity = this.getApplicationContext();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);


        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mButton.getText().equals(startTransfer)) {
                    try {
                        //checkBluetoothStatus();
                        startDialog();
                        mButton.setText(startTransfer);
                    } catch (Exception e) {

                    }

                } else if (mButton.getText().equals(stopTransfer)){
                    try {
                        btSocket.close();
                        mButton.setText(startTransfer);
                    } catch (IOException e) {
                        //
                    }
                }
            }
        });
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        //To Do
        // Data gets sent to solar panels only when accuracy is high
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Reads in sensor data.
        String mString;
         boolean connected = ConnectedThread.connected;
        try {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_PRESSURE:
                     mString = String.format(getString(R.string.displayResult), event.values[0], "mbars");
                    mPressureView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("1" + mString + " ");
                    }

                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "°C");
                    mTempView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("2" + mString + " ");
                    }

                    break;
                case Sensor.TYPE_LIGHT:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "°lx");
                    mLightView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("3" + mString + " ");
                    }

                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "%");
                    mHumidityView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("4" + mString + " ");
                    }

                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "μT");
                    mMagneticView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("5" + mString + " ");
                    }

                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                    SensorManager.getOrientation(mRotationMatrix, mOrientationValues);
                    String mStringAzimuth = String.format(getString(R.string.displayResult), Math.toDegrees(mOrientationValues[0]), "°");
                    String mStringPitch = String.format(getString(R.string.displayResult), Math.toDegrees(mOrientationValues[1]), "°");
                    String mStringRoll = String.format(getString(R.string.displayResult), Math.toDegrees(mOrientationValues[2]), "°");
                    azimuthView.setText(mStringAzimuth);
                    pitchView.setText(mStringPitch);
                    rollView.setText(mStringRoll);
                    if (connected) {
                        mConnectedThread.write("6" + mStringAzimuth + "p" + mStringPitch + "p" + mStringRoll + "p");
                    }

                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            throw e;
        }
    }
    @Override
    protected void onResume() {
        // Registers a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRelativeHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);

        if (mConnectedThread != null) {
            BluetoothDevice device = mBtAdapter.getRemoteDevice(deviceAddress);
            ConnectThread connect = new ConnectThread(device, SensorActivity.this);
            connect.run();
            mConnectedThread = connect.getmConnectedThread();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                msg.setText("Scanning for new devices...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){


            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found;
                msg.setText("Found devices");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.

        //Make sure device is registered first
        unregisterReceiver(mReceiver);
        try {
            if (btSocket != null) {
                btSocket.close();
            }

        } catch (IOException e) {

        }
    }


    @Override
    protected void onPause() {
        // Unregisters the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            deviceAddress = address;

            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
           new bluetoothConnectTask(device).execute();

        }
    };

    private  class bluetoothConnectTask extends AsyncTask<Void, Void, Void> {

        BluetoothDevice device;

        bluetoothConnectTask(BluetoothDevice device) {
            this.device = device;

        }

        @Override
        protected void onPreExecute() {

            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(SensorActivity.this);
                mProgressDialog.setMessage("Connecting...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }

            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // do tracks loading process here, don't update UI directly here because there is different mechanism for it
            ConnectThread connect = new ConnectThread(device, SensorActivity.this);
            connect.run();
           mConnectedThread = connect.getmConnectedThread();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // write display tracks logic here
           mProgressDialog.dismiss();  // dismiss dialog
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }


    private void  startDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(SensorActivity.this);
        alert.setView(R.layout.activity_dialogue);

        alert.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        dialog = alert.create();
        dialog.show();

        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);

        msg = (TextView)dialog.findViewById(R.id.title_paired_devices);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPairedDevicesArrayAdapter.clear();
                mBtAdapter.startDiscovery();

            }
        });


        //               Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<>(dialog.getContext(), R.layout.activity_devices);

        // Find and set up the ListView for paired de`vices
        ListView pairedListView = (ListView) dialog.findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices and append to 'pairedDevices'
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add previosuly paired devices to the array
        if (pairedDevices.size() > 0) {
            // dialog.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable
            msg.setText("Paired devices");
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
            // dialog.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable


        }
    }





}