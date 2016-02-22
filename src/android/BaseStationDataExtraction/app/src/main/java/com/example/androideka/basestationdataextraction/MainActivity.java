package com.example.androideka.basestationdataextraction;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

    private ArrayAdapter<String> adapter;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private HashMap<String, BluetoothDevice> devices = new HashMap<>();

    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

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
                //Toast.makeText(context, device.getName(), Toast.LENGTH_LONG).show();
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
                if (device.getName() != null && device.getName().equals("")) // Use name of board
                {
                    try {
                        bluetooth.cancelDiscovery();
                        long msb = 16;
                        long lsb = 22;
                        UUID uuid = new UUID(msb, lsb);
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
                        socket.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth != null && !bluetooth.isEnabled())
        {
            Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enable, 42);
        }

        registerReceiver(receiver, filter);
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
        if(bluetooth.isDiscovering())
        {
            bluetooth.cancelDiscovery();
        }
        if(bluetooth.startDiscovery())
        {
            Log.d("BLUETOOTH", "Scanning for devices...");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
