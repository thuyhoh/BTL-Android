package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class NavActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private LinearLayout controlBtn;
    private LinearLayout monitorBtn;
    private TextView tv;
    private ImageButton logoutIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nav);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent it = getIntent();
        String username = "Welcome " + it.getStringExtra("Uname");
        Log.i("Uname", username);
        tv = findViewById(R.id.lb1);
        tv.setText(username);
        controlBtn = findViewById(R.id.btnControl);
        controlBtn.setOnClickListener(NavActivity.this);
        monitorBtn = findViewById(R.id.btnMonitor);
        monitorBtn.setOnClickListener(NavActivity.this);
        logoutIV = findViewById(R.id.logoutBtn);
        logoutIV.setOnClickListener(v -> {
            startActivity(new Intent(NavActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public void onClick(View v) {
        if(controlBtn == (LinearLayout) v){
            intent = new Intent(NavActivity.this, ControlActivity.class);

        }else if(monitorBtn == (LinearLayout) v){
            intent = new Intent(NavActivity.this, MonitorActivity.class);
        }else if(logoutIV == (ImageButton) v){
            intent = new Intent(NavActivity.this, LoginActivity.class);
        }
        startActivity(intent);

    }
}