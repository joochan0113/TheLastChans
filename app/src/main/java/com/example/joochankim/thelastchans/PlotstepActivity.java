package com.example.joochankim.thelastchans;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.GregorianCalendar;


public class PlotstepActivity extends AppCompatActivity {

    private static final String TAG = "PlotActivity";
    FirebaseDatabase database;
    DatabaseReference reference;
    GraphView graphView;
    BarGraphSeries series;
    //    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
    //SimpleDateFormat sdfDate = new SimpleDateFormat(("yyyy-MM-dd"));
    SimpleDateFormat sdfHours = new SimpleDateFormat(("hh:mm"));
    Date convertedDate = null;
    DateFormat sdf = new SimpleDateFormat("MM/dd hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plotstep);

        graphView = (GraphView) findViewById(R.id.graphStep);
        series = new BarGraphSeries();
        graphView.addSeries(series);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("BluetoothData");
        series.setColor(Color.rgb(225,90,30));
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(1500);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(false);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(4);
        //Viewport;
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {

                if (isValueX){
                    Date d = new Date((long) (value));
                    return (sdf.format(d));
                    }
                else {
                    return "" + (int) value;
                    //return super.formatLabel(value, isValueX);
                }
            }
        });
        graphView.getGridLabelRenderer().setTextSize(20f);
        graphView.getGridLabelRenderer().reloadStyles();
//        float mzxx = graphView.getX();
//        double mzx = graphView.getViewport().getMaxX(false);
//        double mzn = graphView.getViewport().getMinX(false);
//        //double midz = (mzx - mzn)/2;
//        Log.i(TAG,"What does this number means?" + mzxx +"//"+ mzx);

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
                    timeRawSA[0] = Integer.parseInt(timeRaw.substring(0, 4)); //Year
                    timeRawSA[1] = Integer.parseInt(timeRaw.substring(4, 6)); // Month
                    timeRawSA[2] = Integer.parseInt(timeRaw.substring(6, 8)); // Date
                    timeRawSA[3] = Integer.parseInt(timeRaw.substring(8, 10)); // Hour
                    timeRawSA[4] = Integer.parseInt(timeRaw.substring(10, 12)); // Minute
                    calendar = new GregorianCalendar(timeRawSA[0],timeRawSA[1]-1,timeRawSA[2],timeRawSA[3],timeRawSA[4]);
                    //dateObj = new Date (calendar.getTimeInMillis());
                    timeConverged = calendar.getTimeInMillis();

//                    long timeL = Long.parseLong(graphValue.gettimeValue());
                    double stepsD = Double.parseDouble(graphValue.getstepsValue());
                    PointValue stepValue = new PointValue(timeConverged, stepsD);
                    dp[index] = new DataPoint(stepValue.getxValue(),stepValue.getyValue());
                    long x = stepValue.getxValue();
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

