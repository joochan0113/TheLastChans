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
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;
import java.sql.Timestamp;
import java.util.GregorianCalendar;


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
    SimpleDateFormat sdfHours = new SimpleDateFormat(("hh:mm"));
    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
    Date convertedDate = null;
    DateFormat sdf = new SimpleDateFormat("MM/dd, hh:mm a");

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
        graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
        //Viewport;
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {

                if (isValueX){
                    Date d = new Date((long) (value));
                    return (sdf.format(d));

//                    return sdf.format(new Date((long) value));
                } else {
                    return "" + (int) value;
                    //return super.formatLabel(value, isValueX);
                }
            }
        });
        graphView.getGridLabelRenderer().setTextSize(20f);
        graphView.getGridLabelRenderer().reloadStyles();
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[ (int) dataSnapshot.getChildrenCount()];

                int index = 0;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    GraphValue graphValue = child.getValue(GraphValue.class);

                    String timeRaw;
                    long timeConverged;
                    //Date dateObj = new Date();
                    Calendar calendar;
                    timeRaw = graphValue.gettimeValue();
                    Integer [] timeRawSA = new Integer[5];
                    Log.i(TAG, "What is Time?:" + timeRaw);
                    timeRawSA[0] = Integer.parseInt(timeRaw.substring(0, 4)); //Year
                    timeRawSA[1] = Integer.parseInt(timeRaw.substring(4, 6)); // Month
                    timeRawSA[2] = Integer.parseInt(timeRaw.substring(6, 8)); // Date
                    timeRawSA[3] = Integer.parseInt(timeRaw.substring(8, 10)); // Hour
                    timeRawSA[4] = Integer.parseInt(timeRaw.substring(10, 12)); // Minute
                    calendar = new GregorianCalendar(timeRawSA[0],timeRawSA[1]-1,timeRawSA[2],timeRawSA[3],timeRawSA[4]);
                    //dateObj = new Date (calendar.getTimeInMillis());
                    timeConverged = calendar.getTimeInMillis();

//                    long timeL = Long.parseLong(graphValue.gettimeValue());
                    double hrD = Double.parseDouble(graphValue.gethrValue());
                    PointValue hrValue = new PointValue(timeConverged, hrD);
                    dp[index] = new DataPoint(hrValue.getxValue(),hrValue.getyValue());
                    long x = hrValue.getxValue();
                    graphView.getViewport().setMinX(x-20000000);
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

