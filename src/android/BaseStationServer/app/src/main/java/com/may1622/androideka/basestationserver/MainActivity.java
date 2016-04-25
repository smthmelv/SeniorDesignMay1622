package com.may1622.androideka.basestationserver;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.io.RandomAccessFile;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {

    BluetoothServerSocket server;
    private float duty_cycle = 48;
    private String TAG = "DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView duty_text = (TextView) findViewById(R.id.dutyCycle);
        duty_text.setText("Duty cycle: 48.0");

        try {
            UUID rand = UUID.fromString("f3c99dbc-e8a5-485a-8e06-e8c56b6df710");
            Log.d(TAG, "RANDOM UUID: " + rand);
            server = BluetoothAdapter.getDefaultAdapter()
                    .listenUsingInsecureRfcommWithServiceRecord("server", rand);

            Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverable);

            Log.d(TAG, "Now discoverable to all devices");

            while (true) {


                Log.d(TAG, "Listening for connection request");
                BluetoothSocket sock = server.accept();
                Log.d(TAG, "Connection accepted.");

                InputStream in = sock.getInputStream();
                byte[] request = new byte[5];
                in.read(request, 0, 5);
                if (request[0] == 1) {
                    Log.d(TAG, "Changing duty cycle from " + duty_cycle);
                    byte[] float_buf = new byte[4];
                    System.arraycopy(request, 1, float_buf, 0, float_buf.length);
                    duty_cycle = ByteBuffer.wrap(float_buf)
                            .order(ByteOrder.BIG_ENDIAN).getFloat();
                    duty_text.setText("Duty cycle: " + duty_cycle);
                    Log.d(TAG, "Duty cycle set to " + duty_cycle);
                } else {
                    String filename = "/sdcard/srd/dummy.txt";
                    //File extDir = getExternalFilesDir(null);
                    //File reqFile = new File(extDir, filename);
                    OutputStream out = sock.getOutputStream();
                    RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "r");
                    byte[] buffer = new byte[(int) randomAccessFile.length()];
                    randomAccessFile.read(buffer);
                    out.write(buffer);
                    Log.d("File Sent", "Sent file successfully.");
                    randomAccessFile.close();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
