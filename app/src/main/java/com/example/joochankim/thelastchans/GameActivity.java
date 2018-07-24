package com.example.joochankim.thelastchans;

import android.annotation.SuppressLint;
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
    int bluetoothBtnState;
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

                bluetoothBtnState = 1;
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

                bluetoothBtnState = 2;
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
                bluetoothBtnState = 3;
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
                bluetoothBtnState = 4;
            }
        });

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            String devicename;
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
                devicename = name;
                if (bluetoothBtnState == 1) {
                    connect.setBackgroundColor(Color.rgb(0,255,0));
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            connect.setText("Playing as " + devicename);
                        }
                    }, 5000);
                }
                if (bluetoothBtnState == 2) {
                    mama.setBackgroundColor(Color.argb(50, 255,0,0));
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mama.setText("Playing as " + devicename);
                        }
                    }, 5000);
                }
                if (bluetoothBtnState == 3) {
                    baby.setBackgroundColor(Color.argb(50, 0,0,255));
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            baby.setText("Playing as " + devicename);
                        }
                    }, 5000);
                }
                if (bluetoothBtnState == 4) {
                    gold.setBackgroundColor(Color.argb(50,212,175,55));
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            gold.setText("Playing as " + devicename);
                        }
                    }, 5000);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
                if (bluetoothBtnState == 1) {
                    connect.setBackgroundColor(Color.YELLOW);
                    connect.setText("Connection lost");

                }
                if (bluetoothBtnState == 2) {
                    mama.setBackgroundColor(Color.YELLOW);
                    mama.setText("Connection lost");

                }
                if (bluetoothBtnState == 3) {
                    baby.setBackgroundColor(Color.YELLOW);
                    baby.setText("Connection lost");

                }
                if (bluetoothBtnState == 4) {
                    gold.setBackgroundColor(Color.YELLOW);
                    gold.setText("Connection lost");

                }
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (bluetoothBtnState == 1) {
                            connect.setText(devicename +"Check-in Successful");
                            connect.setBackgroundColor(Color.GREEN);
                        }
                        if (bluetoothBtnState == 2) {
                            mama.setText(devicename +"Check-in Successful");
                            mama.setBackgroundColor(Color.argb(125, 255,0,0));
                        }
                        if (bluetoothBtnState == 3) {
                            baby.setText(devicename +"Check-in Successful");
                            baby.setBackgroundColor(Color.BLUE);
                        }
                        if (bluetoothBtnState == 4) {
                            gold.setText(devicename +"Check-in Successful");
                            gold.setBackgroundColor(Color.argb(125,212,175,55));
                        }
                    }
                }, 5000);
                if (bluetoothBtnState == 1) connect.setClickable(false);
                if (bluetoothBtnState == 2) mama.setClickable(false);
                if (bluetoothBtnState == 3) baby.setClickable(false);
                if (bluetoothBtnState == 4) gold.setClickable(false);
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                if (bluetoothBtnState == 1) {
                    connect.setText("Unable to connect");
                    connect.setBackgroundColor(Color.RED);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            connect.setText("CONNECT");
                            connect.setBackgroundColor(Color.WHITE);
                        }
                    }, 5000);
                }
                if (bluetoothBtnState == 2) {
                    mama.setText("Unable to connect");
                    mama.setBackgroundColor(Color.RED);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mama.setText("CONNECT");
                            mama.setBackgroundColor(Color.WHITE);
                        }
                    }, 5000);
                }
                if (bluetoothBtnState == 3) {
                    baby.setText("Unable to connect");
                    baby.setBackgroundColor(Color.RED);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            baby.setText("CONNECT");
                            baby.setBackgroundColor(Color.WHITE);
                        }
                    }, 5000);
                }
                if (bluetoothBtnState == 4) {
                    gold.setText("Unable to connect");
                    gold.setBackgroundColor(Color.RED);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            gold.setText("CONNECT");
                            gold.setBackgroundColor(Color.WHITE);
                        }
                    }, 5000);
                }
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


    public void onPause() {
        super.onPause();
        bluetooth.disconnect();
    }

}
