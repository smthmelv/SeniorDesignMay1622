package com.example.androideka.basestationdataextraction;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

    private ArrayAdapter<String> adapter;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private HashMap<String, BluetoothDevice> devices = new HashMap<>();

    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    private BluetoothSocket baseStation;
    private BluetoothDevice baseStationDevice;

    private final String TAG = "DEBUG";
    IntentFilter pairFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);

    private final BroadcastReceiver mPairingRequestReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int pin=intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 1234);
                    //the pin in case you need to accept for an specific pin
                    Log.d(TAG, "Start Auto Pairing. PIN = " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY",1234));
                    byte[] pinBytes;
                    pinBytes = (""+pin).getBytes("UTF-8");
                    device.setPin(pinBytes);
                    //setPairing confirmation if needed
                    device.setPairingConfirmation(true);
                    pairDevice(device);
                } catch (Exception e) {
                    Log.e(TAG, "Error occurs when trying to auto pair");
                    e.printStackTrace();
                }
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d(TAG, "Start Pairing... with: " + device.getName());
            device.createBond();
            Log.d(TAG, "Pairing finished.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("NAME", device.getName());
                Log.d("ADDRESS", device.getAddress());
                deviceNames.add(device.getName());
                devices.put(device.getName(), device);
                adapter.notifyDataSetChanged();
                Button search = (Button) findViewById(R.id.findDevices);
                search.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED)
                //|| (ContextCompat.checkSelfPermission(this,
                //Manifest.permission.BLUETOOTH_PRIVILEGED) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH,
                    //Manifest.permission.BLUETOOTH_PRIVILEGED,
                    Manifest.permission.BLUETOOTH_ADMIN},
                    43);
        }
        else
        {
            Log.d("LOG", "Bluetooth permissions enabled");
            Button button = (Button) findViewById(R.id.findDevices);
            button.setVisibility(View.VISIBLE);
        }


        ListView list = (ListView) findViewById(R.id.deviceList);
        adapter  = new ArrayAdapter<>(this, R.layout.device, deviceNames);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device =
                        devices.get(parent.getItemAtPosition(position));
                if (device.getName() != null && device.getName().equals("ci20")) // Use name of board
                {
                    baseStationDevice = device;
                    Log.d(TAG, "Proper station");
                    //try {
                        bluetooth.cancelDiscovery();
                        //BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(
                        //        UUID.fromString("f3c99dbc-e8a5-485a-8e06-e8c56b6df710"));
                        //Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class});
                        //BluetoothSocket tmp = (BluetoothSocket) m.invoke(device, 1);
                        //baseStation = socket;
                        Log.d(TAG, "Socket ready for connection");
                    //} catch (IOException e) {
                    //    Log.d(TAG, "SHIT happened");
                    //    e.printStackTrace();
                    //}
                }
            }
        });

        if(bluetooth != null && !bluetooth.isEnabled())
        {
            Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enable, 42);
        }

        registerReceiver(receiver, filter);
        registerReceiver(mPairingRequestReceiver, pairFilter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        switch(requestCode)
        {
            case 40:
            case 41:
            case 42:
            case 43:
                Button button = (Button) findViewById(R.id.findDevices);
                button.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 42)
        {
            Log.d("BLUETOOTH", "Bluetooth Enabled.");
        }
        else
        {
            Log.e("BLUETOOTH", "Bluetooth not enabled.");
        }
    }

    public void connect(View view)
    {
        if(bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }
        if(bluetooth.startDiscovery())
        {
            Log.d("BLUETOOTH", "Scanning for devices...");
        }
    }

    public void readData(View view)
    {
        try {
            BluetoothSocket sock = baseStationDevice.createInsecureRfcommSocketToServiceRecord(
                    UUID.fromString("f3c99dbc-e8a5-485a-8e06-e8c56b6df710"));
            sock.connect();
            OutputStream request = sock.getOutputStream();
            byte[] reqBuffer = new byte[5];
            reqBuffer[0] = 0;
            request.write(reqBuffer, 0, reqBuffer.length);
            InputStream in = sock.getInputStream();
            byte[] buffer = new byte[65536];
            int num = in.read(buffer);
            Log.d(TAG, num + " bytes received.");
            byte[] fileBuffer = new byte[num];
            for( int i = 0; i < num; i++ )
            {
                fileBuffer[i] = buffer[i];
            }
            File file = new File(getApplicationContext().getFilesDir(), "dummy");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(file));
            Log.d(TAG, "Saving file to internal storage.");
            bufferedOutputStream.write(fileBuffer);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            Toast.makeText(this, num + " bytes received.", Toast.LENGTH_LONG).show();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCycle(View view)
    {
        EditText editText = (EditText) findViewById(R.id.duty_cycle);
        if( editText.getText() != null )
        {
            // Add number of measurements per day to byte[]
            float cycle = Float.parseFloat(editText.getText().toString());
            byte[] reqBuffer = new byte[1];
            reqBuffer[0] = 1;
            byte[] float_buf = ByteBuffer.allocate(4).putFloat(cycle).array();
            byte[] buffer = new byte[reqBuffer.length + float_buf.length];
            System.arraycopy(reqBuffer, 0, buffer, 0, reqBuffer.length);
            System.arraycopy(float_buf, 0, buffer, reqBuffer.length, float_buf.length);
            try {
                BluetoothSocket sock = baseStationDevice.createInsecureRfcommSocketToServiceRecord(
                        UUID.fromString("f3c99dbc-e8a5-485a-8e06-e8c56b6df710"));
                sock.connect();
                OutputStream outputStream = sock.getOutputStream();
                outputStream.write(buffer, 0, buffer.length);
                Toast.makeText(this, "Duty cycle set.", Toast.LENGTH_LONG).show();
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(receiver, filter);
        registerReceiver(mPairingRequestReceiver, pairFilter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(mPairingRequestReceiver);
    }
}
