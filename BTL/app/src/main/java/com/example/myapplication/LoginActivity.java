package com.example.myapplication;

import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgot;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ (Đảm bảo ID trong XML đúng với mấy cái này)
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPassword = findViewById(R.id.edtPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUpLink);
        tvForgot = findViewById(R.id.tvForgotPassword); // Dòng chữ quên mật khẩu

        // --- XỬ LÝ SỰ KIỆN NÚT LOGIN ---
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // 1. Kiểm tra xem có để trống không
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập Mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Báo hiệu đang chạy
                Toast.makeText(LoginActivity.this, "Đang xử lý đăng nhập...", Toast.LENGTH_SHORT).show();

                // 3. Gửi lệnh đăng nhập lên Firebase
                loginUser(email, password);
            }
        });

        // Chuyển sang trang Đăng ký
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Chuyển sang trang Quên mật khẩu (Nếu bạn đã tạo Activity này)
        if (tvForgot != null) {
            tvForgot.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            });
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // --- ĐĂNG NHẬP THÀNH CÔNG ---

                            // 1. Hiện thông báo
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            // 2. Chuyển sang màn hình chính (MainActivity)
                            Intent intent = new Intent(LoginActivity.this, NavActivity.class);
                            startActivity(intent);

                            // 3. Đóng màn hình Login lại (để bấm Back không quay lại đây nữa)
                            finish();

                        } else {
                            // --- ĐĂNG NHẬP THẤT BẠI ---
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}