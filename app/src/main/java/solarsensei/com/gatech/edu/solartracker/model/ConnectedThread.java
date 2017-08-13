package solarsensei.com.gatech.edu.solartracker.model;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

import solarsensei.com.gatech.edu.solartracker.R;
import solarsensei.com.gatech.edu.solartracker.controllers.SensorActivity;


/**
 * Created by timothybaba on 8/7/17.
 */

//create new class for connect thread
 public class ConnectedThread extends Thread {
    private final OutputStream mmOutStream;
    private final BluetoothSocket mSocket;
    public static boolean connected = false;
    private AlertDialog dialog = SensorActivity.dialog;
    private Activity activity;
    private TextView transmitView;
    private Button buttonView;


    //creation of the connect thread
    public ConnectedThread(BluetoothSocket socket, Activity activity) {
        this.activity = activity;
        this.mSocket = socket;
        transmitView = (TextView) activity.findViewById(R.id.transmitStatus);
        buttonView = (Button) activity.findViewById(R.id.startPairing);

        OutputStream tmpOut = null;

        try {
            //Create output stream for connection
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmOutStream = tmpOut;
    }

    //write method
    public void write(final String input) {
        byte[] msgBuffer = input.getBytes();  //converts entered String into bytes
        try {
            mmOutStream.write(msgBuffer); //write bytes over BT connection via OutputStream
            if (!connected) {
                connected = true;
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast =  Toast.makeText(dialog.getContext(), "Connected!",
                                Toast.LENGTH_SHORT);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        v.setTextColor(Color.GREEN);
                        toast.show();
                    }
                });
            }
            dialog.dismiss();


            activity.runOnUiThread(new Runnable() {
                public void run() {
                    transmitView.setText(R.string.sendData);
                    buttonView.setText(R.string.Stop);

                }
            });


        } catch (IOException e) {
            connected = false;

            dialog.dismiss();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast =  Toast.makeText(activity.getBaseContext(), "Couldn't send " + input + " to the other device ",
                            Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    v.setTextColor(Color.RED);
                    toast.show();
                }
            });

            transmitView.setText("");

        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
        }
    }

}
