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
import com.google.firebase.auth.FirebaseAuthException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnReset;
    private TextView tvBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtEmail = findViewById(R.id.edtEmailForgot);
        btnReset = findViewById(R.id.btnResetPass);
        tvBack = findViewById(R.id.tvBackToLogin);

        // ================== NÚT GỬI EMAIL RESET ==================
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();

                // Kiểm tra email
                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Vui lòng nhập Email");
                    edtEmail.requestFocus();
                    return;
                }

                Toast.makeText(ForgotPasswordActivity.this,
                        "Đang gửi email khôi phục...", Toast.LENGTH_SHORT).show();

                // Gửi yêu cầu reset mật khẩu
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(ForgotPasswordActivity.this,
                                            "Đã gửi link khôi phục mật khẩu!\nVui lòng kiểm tra Email.",
                                            Toast.LENGTH_LONG).show();

                                    finish(); // Quay về Login

                                } else {

                                    String message = "Gửi email thất bại!";

                                    if (task.getException() instanceof FirebaseAuthException) {
                                        String errorCode = ((FirebaseAuthException)
                                                task.getException()).getErrorCode();

                                        switch (errorCode) {
                                            case "ERROR_INVALID_EMAIL":
                                                message = "Email không đúng định dạng!";
                                                break;

                                            case "ERROR_USER_NOT_FOUND":
                                                message = "Email chưa được đăng ký!";
                                                break;

                                            case "ERROR_TOO_MANY_REQUESTS":
                                                message = "Thao tác quá nhiều lần, vui lòng thử lại sau!";
                                                break;

                                            default:
                                                message = "Lỗi hệ thống, vui lòng thử lại!";
                                                break;
                                        }
                                    }

                                    Toast.makeText(ForgotPasswordActivity.this,
                                            message, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // ================== NÚT QUAY LẠI ==================
        tvBack.setOnClickListener(v -> finish());
    }
}