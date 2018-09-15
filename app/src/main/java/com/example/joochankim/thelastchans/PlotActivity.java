package com.example.joochankim.thelastchans;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlotActivity extends AppCompatActivity {

    private static final String TAG = "PlotActivity";
    private DatabaseReference mReference, mRef;
    FirebaseDatabase database;
    DatabaseReference reference;
    EditText yValue;
    GraphView graphView;
    LineGraphSeries series;
    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
    //SimpleDateFormat sdfDate = new SimpleDateFormat(("yyyy-MM-dd"));
    SimpleDateFormat sdfHours = new SimpleDateFormat(("hh:mm:ss"));
    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        graphView = (GraphView) findViewById(R.id.graphSignal);
        series = new LineGraphSeries();
        graphView.addSeries(series);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("BluetoothData");

        yValue = (EditText) findViewById(R.id.y_Value_);
//        btn_insert = (Button) findViewById(R.id.btn_insert);
        //SETTING COLORS of the Graph
        //series.setColor(Color.GREEN);
        series.setColor(Color.rgb(225,90,30));
        //Set Thickness of the graph -> series.setThickness(#);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(60,200,0,0));
        series.setDrawDataPoints(true); //setDataPointsRadius(#radius)
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(100.0);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(false);
        graphView.getViewport().setYAxisBoundsManual(true);



        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        //Viewport;
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {

                if (isValueX){
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataPoint[] dp = new DataPoint[ (int) dataSnapshot.getChildrenCount()];
                Log.i(TAG, "BAAAM: "+ dp.length);
                int index = 0;

                for(DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){

                    PointValue pointValue = mDataSnapshot.getValue(PointValue.class);
                    dp[index] = new DataPoint(pointValue.getxValue(),pointValue.getyValue());
                    Log.i(TAG, "BMES:" + pointValue);
                    long x = pointValue.getxValue();
                    graphView.getViewport().setMinX(x-400);
                    graphView.getViewport().setMaxX(x);
                    graphView.getViewport().setXAxisBoundsManual(true);

                    index++;
                }
                series.resetData(dp);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                DataPoint[] dp = new DataPoint[ (int) dataSnapshot.getChildrenCount()];
                Log.i(TAG, "BAAAH: "+ dp.length);
                int index = 0;

                for(DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){

                    PointValue pointValue = mDataSnapshot.getValue(PointValue.class);
                    dp[index] = new DataPoint(pointValue.getxValue(),pointValue.getyValue());
                    Log.i(TAG, "BMES:" + pointValue);
                    long x = pointValue.getxValue();
                    graphView.getViewport().setMinX(x-400);
                    graphView.getViewport().setMaxX(x);
                    graphView.getViewport().setXAxisBoundsManual(true);
                    index++;
                }
                series.resetData(dp);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "foo");
            }
        });

    }
}

