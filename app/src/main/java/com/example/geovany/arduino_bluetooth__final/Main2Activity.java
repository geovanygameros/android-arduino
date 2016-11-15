package com.example.geovany.arduino_bluetooth__final;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnEstancia, btnCochera, btnSala, btnCocheraEstancia, btnEstanciaSala, btnCocheraSala;
    char estancia = '2';
    char cochera = '3';
    char sala = '1';
    char cocheraSala = '4';
    char estanciaSala = '5';
    char cocheraEstancia = '6';
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //MAC-address of BT
    private static final String MAC_ADDRESS= "20:16:08:29:05:10";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opciones, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {

        //Toast.makeText(getBaseContext(), "cerrar", Toast.LENGTH_LONG).show();
        //System.runFinalizersOnExit(true);

        super.onPause();
    }

    @Override
    protected void onStop() {
        System.exit(0);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnCochera = (ImageButton)findViewById(R.id.btnCochera);
        btnEstancia = (ImageButton) findViewById(R.id.btnEstancia);
        btnSala = (ImageButton) findViewById(R.id.btnSala);
        btnCocheraEstancia = (ImageButton) findViewById(R.id.btnCocheraEstancia);
        btnEstanciaSala = (ImageButton) findViewById(R.id.btnEstanciaSala);
        btnCocheraSala = (ImageButton) findViewById(R.id.btnCocheraSala);

        btnCochera.setOnClickListener(this);
        btnEstancia.setOnClickListener(this);
        btnSala.setOnClickListener(this);
        btnCocheraEstancia.setOnClickListener(this);
        btnEstanciaSala.setOnClickListener(this);
        btnCocheraSala.setOnClickListener(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(MAC_ADDRESS);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.

        try {
            btSocket.connect();

        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.


        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }


    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {

            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), "Necesitas reiniciar la aplicación", Toast.LENGTH_LONG).show();
        //finish();
    }

    public void sendData(char message) {



        try {
            outStream.write(message);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (MAC_ADDRESS.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";


            errorExit("Fatal Error", msg);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCochera:
                sendData(cochera);
                break;
            case R.id.btnEstancia:
                sendData(estancia);
                break;
            case R.id.btnSala:
                sendData(sala);
                break;
            case R.id.btnCocheraEstancia:
                sendData(cocheraEstancia);
                break;
            case R.id.btnCocheraSala:
                sendData(cocheraSala);
                break;
            case R.id.btnEstanciaSala:
                sendData(estanciaSala);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.todasAreasOn:
                sendData('7');
                break;
            case R.id.todasAreasOff:
                sendData('8');
                break;

        }
        return true;
    }
}
