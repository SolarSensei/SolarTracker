package solarsensei.com.gatech.edu.solartracker.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;
import solarsensei.com.gatech.edu.solartracker.controllers.MainActivity;
import solarsensei.com.gatech.edu.solartracker.controllers.SensorActivity;

/**
 * Created by timothybaba on 8/7/17.
 */

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    //Standard SerialPortService ID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBtAdapter = MainActivity.mBtAdapter;
    private ConnectedThread mConnectedThread;
    private  Activity activity;
    private AlertDialog dialog = SensorActivity.dialog;

    public ConnectThread(BluetoothDevice device, Activity activity) {
        BluetoothSocket tmp = null;
        this.activity = activity;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            tmp = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        } catch (IOException e) {
            Toast.makeText(activity.getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
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
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast =  Toast.makeText(dialog.getContext(), "No connection UUID match found!",
                                Toast.LENGTH_SHORT);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        v.setTextColor(Color.RED);
                        toast.show();
                    }
                });

            } catch (IOException closeException) {
                Toast.makeText(activity.getBaseContext(), "could not close socket", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.

        SensorActivity.mProgressDialog.dismiss();
        manageMyConnectedSocket(mmSocket);

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket btSocket) {
        mConnectedThread = new ConnectedThread(btSocket, activity);
        mConnectedThread.start();
        //I send a character when beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        try {
            mConnectedThread.write("x");
        } catch (Exception e) {

        }
    }

    //returns the connected Thread
    public ConnectedThread getmConnectedThread() {
        return mConnectedThread;
    }
}
