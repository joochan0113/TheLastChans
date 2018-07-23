package com.example.joochankim.thelastchans;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class GameActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference referenceDoc;
    BluetoothSPP bluetooth;
    private final Handler handler = new Handler();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat(("yyyy-MM-dd"));
    SimpleDateFormat sdfHours = new SimpleDateFormat(("hh:mm:ss"));
    ImageView map;
    int move;
    TextView movep;
    private static final String TAG = "GameActivity";
    Button connect, mama, baby, gold;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        map = (ImageView) findViewById(R.id.map);
        movep = (TextView) findViewById(R.id.movep);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("GameData");
        referenceDoc = reference.child("Player1");
        bluetooth = new BluetoothSPP(this);

        connect = (Button) findViewById(R.id.connect);
        mama = (Button) findViewById(R.id.mama);
        baby = (Button) findViewById(R.id.baby);
        gold = (Button) findViewById(R.id.gold);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
                GameActivity.this.BTCheck(connect);
            }
        });
        mama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
                GameActivity.this.BTCheck(mama);
            }
        });
        baby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
                GameActivity.this.BTCheck(baby);
            }
        });
        gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
                GameActivity.this.BTCheck(gold);
            }
        });



        referenceDoc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String player1 = (String) dataSnapshot.getValue();
                Log.i(TAG, "Hello");
                movep.setText(player1);
                move = Integer.parseInt(movep.getText().toString());
                moveAP(move);
            }

            private void moveAP(int move) {
                if (move <= 0) {
                    map.setImageResource(R.drawable.monostart);
                } else if (move == 1) {
                    map.setImageResource(R.drawable.mono1);
                } else if (move == 2) {
                    map.setImageResource(R.drawable.mono2);
                } else if (move >= 3) {
                    map.setImageResource(R.drawable.mono3);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void BTCheck (final Button somebut) {
        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            String devicename;
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
                somebut.setText("Connected to " + name);
                if (somebut == connect) {
                    somebut.setBackgroundColor(Color.rgb(0,255,0));
                }
                if (somebut == mama) {
                    somebut.setBackgroundColor(Color.argb(50, 255,0,0));
                }
                if (somebut == baby) {
                    somebut.setBackgroundColor(Color.argb(50, 0,0,255));
                }
                if (somebut == gold) {
                    somebut.setBackgroundColor(Color.argb(50,212,175,55));
                }
                devicename = name;
                handler.postDelayed(new Runnable() {
                    public void run() {
                        somebut.setText("Playing as " + devicename);
                    }
                }, 5000);
            }

            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
                somebut.setBackgroundColor(Color.YELLOW);
                somebut.setText("Connection lost");
                handler.postDelayed(new Runnable() {
                    public void run() {
                        somebut.setText(devicename +"Check-in Successful");
                        if (somebut == connect) {
                            somebut.setBackgroundColor(Color.GREEN);
                        }
                        if (somebut == mama) {
                            somebut.setBackgroundColor(Color.argb(125, 255,0,0));
                        }
                        if (somebut == baby) {
                            somebut.setBackgroundColor(Color.BLUE);
                        }
                        if (somebut == gold) {
                            somebut.setBackgroundColor(Color.argb(125,212,175,55));
                        }
                    }
                }, 5000);
                somebut.setClickable(false);
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                somebut.setText("Unable to connect");
                somebut.setBackgroundColor(Color.RED);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        somebut.setText("CONNECT");
                        somebut.setBackgroundColor(Color.LTGRAY);
                    }
                }, 5000);
            }
        });
    }
    public void onPause() {
        super.onPause();
        bluetooth.disconnect();
    }

}
