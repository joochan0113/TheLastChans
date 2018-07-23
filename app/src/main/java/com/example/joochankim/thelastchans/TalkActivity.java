package com.example.joochankim.thelastchans;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TalkActivity extends AppCompatActivity {

    public EditText mValueField;
    TextView addedText;
    Button mAddBtn;
    public DatabaseReference mRootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);


        mRootRef = FirebaseDatabase.getInstance().getReference("Notes");

        addedText = findViewById(R.id.valueField2);

        mRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                addedText.setText(value);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                addedText.setText(value);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mValueField = (EditText) findViewById(R.id.valueField);
        mAddBtn = (Button) findViewById(R.id.addBtn);

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String key = "Notes";
                String value = mValueField.getText().toString();

                DatabaseReference childRef = mRootRef.child(key);
                childRef.setValue(value);
                mValueField.setText("");
            }
        });


    }



}
