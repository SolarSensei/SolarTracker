package solarsensei.com.gatech.edu.solartracker;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

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
    private TextView connectionStatus;
    private ListView pairedDevices;
    private TextView msg;
    private TextView transmitView;

    private  AlertDialog dialog;

    private ProgressDialog mProgressDialog;

    private SensorManager mSensorManager;

    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private BluetoothAdapter mBtAdapter;

    private boolean connected = false;

    //Environmental sensors
    private Sensor mPressure;
    private Sensor mTemperature;
    private Sensor mLight;
    private Sensor mRelativeHumidity;
    private Sensor mMagneticField;

    //motion sensors
    private Sensor mRotation;

    //constants
    private final static int REQUEST_ENABLE_BT = 1;

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    final int handlerState = 0;
    private BluetoothSocket btSocket = null;

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
        transmitView = (TextView) findViewById(R.id.transmitStatus);


        // Gets an instance of the sensor service, and uses that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mRelativeHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBluetoothStatus();



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
        try {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_PRESSURE:
                     mString = String.format(getString(R.string.displayResult), event.values[0], "mbars");
                    mPressureView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("1" + mString);
                    }

                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "°C");
                    mTempView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("2" + mString);
                    }

                    break;
                case Sensor.TYPE_LIGHT:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "°lx");
                    mLightView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("3" + mString);
                    }

                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "%");
                    mHumidityView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("4" + mString);
                    }

                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mString = String.format(getString(R.string.displayResult), event.values[0], "μT");
                    mMagneticView.setText(mString);
                    if (connected) {
                        mConnectedThread.write("5" + mString);
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
                        mConnectedThread.write("6A" + mStringAzimuth + "P" + mStringPitch + "R" + mStringRoll);
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
                System.out.println("Yes found");
                //bluetooth device found;
                msg.setText("Found devices");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.

        //Make sure device is registered first
//        unregisterReceiver(mReceiver);
    }


    @Override
    protected void onPause() {
        // Unregisters the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
//        try
//        {
//            //Don't leave Bluetooth sockets open when leaving activity
//            btSocket.close();
//        } catch (IOException e2) {
//            //insert code to deal with this
//        }
    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

           // connectionStatus.setText("Connecting...");
            //showProgressDialog();



            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
           new bluetoothConnectTask(device, mProgressDialog).execute();

            // Make an intent to start next activity while taking an extra which is the MAC address.
//            Intent i = new Intent(DeviceListActivity.this, MainActivity.class);
//            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
//            startActivity(i);



        }
    };

    private  class bluetoothConnectTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog mProgressDialog;
        BluetoothDevice device;

        bluetoothConnectTask(BluetoothDevice device, ProgressDialog mProgressDialog) {
            this.device = device;
            this.mProgressDialog = mProgressDialog;

        }

        @Override
        protected void onPreExecute() {

            if (SensorActivity.this.mProgressDialog == null) {
                SensorActivity.this.mProgressDialog = new ProgressDialog(SensorActivity.this);
                SensorActivity.this.mProgressDialog.setMessage("Connecting...");
                SensorActivity.this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }

            SensorActivity.this.mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // do tracks loading process here, don't update UI directly here because there is different mechanism for it
            ConnectThread connect = new ConnectThread(device);
            connect.run();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // write display tracks logic here
            SensorActivity.this.mProgressDialog.dismiss();  // dismiss dialog
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    private void checkBluetoothStatus() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getBaseContext(), "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (!mBtAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                startDialog();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode != 0){
                // bluetooth enabled
                startDialog();

            } else{
                Toast.makeText(getBaseContext(), "You must enable bluetooth to transfer data", Toast.LENGTH_LONG).show();
            }
        }


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
                IntentFilter filter = new IntentFilter();

                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                registerReceiver(mReceiver, filter);


            }
        });

        connectionStatus = (TextView) dialog.findViewById(R.id.connecting);
        connectionStatus.setText(" ");
        connectionStatus.setTextSize(40);

        //               Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<>(dialog.getContext(), R.layout.activity_devices);

        // Find and set up the ListView for paired devices
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

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final OutputStream mmOutStream;


        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmOutStream = tmpOut;
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                System.out.println(input);
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
                if (!connected) {
                    connected = true;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast =  Toast.makeText(dialog.getContext(), "Connected!",
                                    Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            v.setTextColor(Color.GREEN);
                            dialog.dismiss();
                            toast.show();
                        }
                    });
                }
                transmitView.setText("sending data...");

            } catch (IOException e) {
                //if you cannot write, close the application
                //Toast toast = Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_SHORT).show();
                connected = false;
                System.out.println("connected: " + connected);


                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast =  Toast.makeText(dialog.getContext(), "Connection Failure ",
                                Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        v.setTextColor(Color.RED);
                        toast.show();
                    }
                });

                transmitView.setText("");

              //  think about this
                //finish();
            }
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBtAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.

                try {
                    mmSocket.close();

                } catch (IOException closeException) {
                    Toast.makeText(getBaseContext(), "could not close socket", Toast.LENGTH_LONG).show();
                }
               // return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.

            mProgressDialog.dismiss();
//
            manageMyConnectedSocket(mmSocket);

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
               // Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket btSocket) {
        //Toast.makeText(getBaseContext(), "connected!", Toast.LENGTH_LONG).show();
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        try {
            mConnectedThread.write("x");
            //Toast.makeText(getBaseContext(), "connected!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }



}