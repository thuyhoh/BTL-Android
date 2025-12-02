package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnReset;
    private TextView tvBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmailForgot);
        btnReset = findViewById(R.id.btnResetPass);
        tvBack = findViewById(R.id.tvBackToLogin);

        // --- XỬ LÝ NÚT GỬI LINK ---
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();

                // 1. Kiểm tra rỗng
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Thông báo đang gửi
                Toast.makeText(ForgotPasswordActivity.this, "Đang gửi email...", Toast.LENGTH_SHORT).show();

                // 3. Gửi lệnh lên Firebase
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Thành công
                                    Toast.makeText(ForgotPasswordActivity.this, "Đã gửi link! Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show();
                                    finish(); // Đóng màn hình này để quay về Login
                                } else {
                                    // Thất bại (Email không tồn tại hoặc lỗi mạng)
                                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // --- NÚT QUAY LẠI ---
        tvBack.setOnClickListener(v -> finish());
    }
}