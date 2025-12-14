package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MonitorActivity extends AppCompatActivity {

    private TextView lbTemp, lbHumi, lbTime, lbFans;
    private ImageButton backBtn;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monitor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseReference = firebaseDatabase.getReference("toAND/");

        lbTemp = (TextView) findViewById(R.id.tvTemp);
        lbHumi = (TextView) findViewById(R.id.tvHumi);
        lbTime = (TextView) findViewById(R.id.tvTime);
        lbFans = (TextView) findViewById(R.id.tvFans);
        backBtn = (ImageButton)findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(MonitorActivity.this, NavActivity.class));
            finish();
        });
        GetData();

    }

    private void GetData()
    {
        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toAND result = snapshot.getValue(toAND.class);
                if(result != null){
                    lbTemp.setText(Integer.toString(result.Temp));
                    lbHumi.setText(Integer.toString(result.Humi));
                    lbTime.setText(Integer.toString(result.Time));
                    if(result.Fans)
                    {
                        lbFans.setText("OFF");
                    }else{
                        lbFans.setText("ON");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonitorActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}