package com.example.joochankim.thelastchans;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

//    final String ON = "1";
//    final String OFF = "0";
    private final Handler handler = new Handler();
    BluetoothSPP bluetooth;
    Button connect;
    Button btnTalk;
    ImageView ImageBOT, iconGame, iconPlot;
    TextView textBOT, btTxt, txtGame, txtPlot;
    Drawable drmorelli;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat(("yyyy-MM-dd"));
    SimpleDateFormat sdfHours = new SimpleDateFormat(("hh:mm:ss"));

    //FIREBASE
    private FirebaseDatabase mDb;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = new BluetoothSPP(this);

        connect = findViewById(R.id.connect);
//        on = findViewById(R.id.on);
//        off = findViewById(R.id.off);
        iconPlot = findViewById(R.id.iconPlot);
        txtPlot = findViewById(R.id.txtPlot);
        btnTalk = findViewById(R.id.btnTalk);
        iconGame = findViewById(R.id.iconGame);
        txtGame = findViewById(R.id.txtGame);
        btTxt = findViewById(R.id.btTxt);
        ImageBOT = findViewById(R.id.imageView);
        textBOT = findViewById(R.id.textBOT);
        drmorelli = getResources().getDrawable(R.drawable.dr_peter_morelli);

        //FIREBASE
        mDb = FirebaseDatabase.getInstance();
        mRef = mDb.getReference();

        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @SuppressLint("SetTextI18n")
            public void onDeviceConnected(String name, String address) {
                connect.setText("Connected to " + name);
                ImageBOT.setImageResource(R.drawable.dr_peter_morelli);
                textBOT.setText("Dr. Morelli is now watching you. So let's keep it UP!!");
            }
            @SuppressLint("SetTextI18n")
            public void onDeviceDisconnected() {
                connect.setBackgroundColor(Color.YELLOW);
                connect.setText("Connection lost");
                textBOT.setText("See you soon!");
                ImageBOT.setImageResource(R.drawable.victoryroyale);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ImageBOT.setImageResource(R.drawable.team);
                        textBOT.setText("Welcome");
                        connect.setText("CONNECT");
                        connect.setBackgroundColor(Color.LTGRAY);
                    }
                }, 5000);
            }
            //onDeviceConnectionFailed(
            @SuppressLint("SetTextI18n")
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                connect.setText("Unable to connect");
                connect.setBackgroundColor(Color.RED);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        connect.setText("CONNECT");
                        ImageBOT.setImageResource(R.drawable.team);
                        connect.setBackgroundColor(Color.LTGRAY);
                    }
                }, 5000);

            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
        txtPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PlotActivity.class);
                startActivity(i);
            }
        });
        iconPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PlotActivity.class);
                startActivity(i);
            }
        });
        btnTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TalkActivity.class);
                startActivity(i);
            }
        });
        iconGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });
        txtGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

//        //Send data to Arduino
//        on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bluetooth.send(ON, true);
//            }
//        });
//        off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bluetooth.send(OFF, true);
//            }
//        });

        //Receive Data from Arduino
        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataReceived(byte[] data, String message) {
                btTxt.setText(message);
                long date = System.currentTimeMillis();
                String dateString = sdf.format(date);
                String dateonlyString = sdfDate.format(date);
                String hourString = sdfHours.format(date);


                int endOfLineIndex = message.indexOf("~");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = message.substring(0, endOfLineIndex);    // extract string
                    btTxt.setText("Data Received = " + dataInPrint);

                    if (message.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                    {
                        String sensor0 = message.substring(1, 5);             //get sensor value from string between indices 1-5
                        String sensorRaw = message.substring(6, 8);            //same again...
                        String sensor2 = message.substring(13, 17);
                        //String sensor3 = ;
                        //sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
                        //sensorViewRaw.setText(" Sensor 0 Raw = " + sensorRaw);
                        //sensorView2.setText(" Sensor HR = " + sensor2 + "BPM");
                        //sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        long x = new Date().getTime();
                        double y = Double.parseDouble(sensorRaw);
                        PointValue pointValue = new PointValue(x,y);

                        HashMap<String, String> dataMap = new HashMap<String, String>();
                        dataMap.put("Voltage", sensor0);
                        dataMap.put("Raw", sensorRaw);
                        dataMap.put("HR", sensor2);
                        //dataMap.put("time", sensor3);
                        mRef.child("BluetoothDatas").child(dateString).setValue(dataMap);
                        mRef.child("BluetoothData").child(dateonlyString).child(hourString).setValue(pointValue);
                    }
                }

            }
        });

    }

    public void onStart() {
        super.onStart();
        if (!bluetooth.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            if (bluetooth.isBluetoothEnabled()) {
                Toast.makeText(getApplicationContext(), "Bluetooth is ON", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please turn on Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }


    public void onDestroy() {
        super.onDestroy();
        bluetooth.stopService();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            connect.setText("Attempting to connect to: " + BluetoothDevice.EXTRA_NAME);
            if (resultCode == Activity.RESULT_OK)
                bluetooth.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
