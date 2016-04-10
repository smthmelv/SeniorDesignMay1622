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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.UUID;
import java.io.RandomAccessFile;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {

    BluetoothServerSocket server;
    private String TAG = "DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Hello World.");

        try{
            UUID rand = UUID.fromString("f3c99dbc-e8a5-485a-8e06-e8c56b6df710");
            Log.d(TAG, "RANDOM UUID: " + rand);
            server = BluetoothAdapter.getDefaultAdapter()
                    .listenUsingInsecureRfcommWithServiceRecord("server", rand);

            Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverable);

            Log.d(TAG, "Now discoverable to all devices");
            Log.d(TAG, "Listening for connection request");
            BluetoothSocket sock = server.accept();
            Log.d(TAG, "Connection accepted.");
            //sock.connect();

            String filename = "/sdcard/srd/dummy.txt";
            File extDir = getExternalFilesDir(null);
            File reqFile = new File(extDir, filename);
            OutputStream out = sock.getOutputStream();
            RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "r");
            byte[] buffer = new byte[(int)randomAccessFile.length()];
            randomAccessFile.read(buffer);
            out.write(buffer);
            Log.d("File Sent", "Sent file successfully.");
            randomAccessFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
