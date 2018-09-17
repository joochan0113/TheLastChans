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
import android.util.Log;
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
    final String StartBT = "S";
    BluetoothSPP bluetooth;
    Button connect;
    ImageView ImageBOT, iconPlot, iconTalk;
    TextView textBOT, btTxt, txtPlot, txtTalk;
    Drawable drmorelli;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat(("yyyy-MM-dd"));
    SimpleDateFormat sdfHours = new SimpleDateFormat(("hh:mm:ss"));
    private static final String TAG = "MainActivity";
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
        iconTalk = findViewById(R.id.talkIcon);
        txtPlot = findViewById(R.id.txtPlot);
        txtTalk = findViewById(R.id.talkTxt);
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
                bluetooth.send("S", false);
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
        iconTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TalkActivity.class);
                startActivity(i);
            }
        });
        txtTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TalkActivity.class);
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

                Log.i(TAG, "aaaaaaaaaaaaaaa"+ message);


                String rYear, rMonth, rDate, rHour, rMin;

                String boxNum, receivedDatesString, receivedSignalString1, receivedSignalString2;
                String [] receivedDates = new String[0];
                String [] receivedSignal1 = new String[0];
                String [] receivedSignal2 = new String[0];
                String [] boxStringArray = new String[0];
                String [] rBTALLlines = new String[0];
                int endOfLineIndex = message.indexOf("%");                    // it's not end of line, but just there to keep the for-loop going :) Nothing much
                rBTALLlines = message.split("~",-4);

                if (rBTALLlines[1].charAt(0)== '&'){  //Time
                    int stringSize = rBTALLlines[1].length();
                    receivedDatesString = message.substring(1, stringSize);
                    receivedDates = receivedDatesString.split("A", -4);
                    Log.i(TAG, "AHAHAHAHAHAHAHAHAHAHAHAHAHAHA"+ String.valueOf(receivedDates[0]));
                    Log.i(TAG, message);
                }

                if (rBTALLlines[0].charAt(0) == '#') {       //SIGNAL 1
                    int stringSize = rBTALLlines[0].length();
                    receivedSignalString1 = message.substring(1, stringSize);
                    receivedSignal1 = receivedSignalString1.split("A", -4);
                    Log.i(TAG, "WWWWWWWWWWWWWWWWWWWWWWWWWWW"+ String.valueOf(receivedSignal1[0]));
                }

                if (rBTALLlines[2].charAt(0)== '%'){         //SIGNAL 2
                    int stringSize = rBTALLlines[2].length();
                    receivedSignalString2 = message.substring(1, stringSize);
                    receivedSignal2 = receivedSignalString2.split("A", -2);
                    Log.i(TAG, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+ String.valueOf(receivedSignal2[0]));

                }

                if (message.contains("BoxNumber")) {
                    boxStringArray = message.split("BoxNumber", -1);
                    boxNum = boxStringArray[1];
                    mRef.child("GameData").child(bluetooth.getConnectedDeviceAddress()).setValue(boxNum);
                }

                for (int index = 0; index <= endOfLineIndex; index++) {
                    long time = Long.parseLong(receivedDates[index]);
                    rYear = receivedDates[index].substring(0, 4);
                    rMonth = receivedDates[index].substring(4, 6);
                    rDate = receivedDates[index].substring(6, 8);
                    rHour = receivedDates[index].substring(8, 10);
                    rMin = receivedDates[index].substring(10, 12);

                    if (receivedSignal1[0] != null || receivedSignal2[0] !=null) {
                        double bpm = Double.parseDouble(receivedSignal1[index]);
                        double steps = Double.parseDouble(receivedSignal2[index]);


                        PointValue pointBPM = new PointValue(time, bpm);
                        PointValue pointSteps = new PointValue(time, steps);

                        HashMap<String, Double> dataSignals = new HashMap<String, Double>();
                        HashMap<String, Long> dataMap = new HashMap<String, Long>();
                        dataSignals.put("Heart Rate", bpm);
                        dataSignals.put("Steps", steps);
                        dataMap.put("Raw", time);
                        mRef.child("BluetoothDatas").child(rYear + rMonth + rDate).setValue(dataMap);
                        mRef.child("BluetoothData").child(rYear + rMonth + rDate).setValue(pointBPM);
                    }
                }
            } //End of Receiving String Values from Arduino
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
            connect.setText("Attempting to connect");
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
