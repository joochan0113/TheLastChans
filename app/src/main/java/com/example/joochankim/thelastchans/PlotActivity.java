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
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;
import java.sql.Timestamp;


public class PlotActivity extends AppCompatActivity {

    private static final String TAG = "PlotActivity";
    private DatabaseReference mReference, mRef;
    FirebaseDatabase database;
    DatabaseReference reference;
    EditText yValue;
    GraphView graphView;
    LineGraphSeries series;
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
    //SimpleDateFormat sdfDate = new SimpleDateFormat(("yyyy-MM-dd"));
    SimpleDateFormat sdfHours = new SimpleDateFormat(("hh:mm:ss"));
    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
    Date convertedDate = null;
    DateFormat sdf = new SimpleDateFormat("MM/dd, hh:mm");

    public String convertTime(long time){
//        Date date = new Date(time);
//        Format format = new SimpleDateFormat("yyyyMMddhhmm");
//        return format.format(date);
        Date date;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        return df.format(new Date(time));
    }


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
        graphView.getViewport().setMaxY(200.0);
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
                    return sdf.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[ (int) dataSnapshot.getChildrenCount()];
//                Log.i(TAG, "BAAAH: "+ dp.length);
                int index = 0;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    GraphValue graphValue = child.getValue(GraphValue.class);
                    long timeL = Long.parseLong(graphValue.gettimeValue());
                    double hrD = Double.parseDouble(graphValue.gethrValue());
                    Log.i(TAG, "D"+convertTime(timeL));

                    PointValue hrValue = new PointValue(Long.parseLong(convertTime(timeL)), hrD);
                    dp[index] = new DataPoint(hrValue.getxValue(),hrValue.getyValue());
                    long x = hrValue.getxValue();
                    graphView.getViewport().setMinX(x-400);
                    graphView.getViewport().setMaxX(x);
                    graphView.getViewport().setXAxisBoundsManual(true);
                    index++;
                }
                series.resetData(dp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

