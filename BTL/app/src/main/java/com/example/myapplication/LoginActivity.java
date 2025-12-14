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
import com.google.firebase.auth.FirebaseAuthException;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgot;
    private FirebaseAuth mAuth;
    String password;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPassword = findViewById(R.id.edtPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUpLink);
        tvForgot = findViewById(R.id.tvForgotPassword);

        // ================== NÚT ĐĂNG NHẬP ==================
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();

                // ===== KIỂM TRA EMAIL =====
                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Vui lòng nhập Email");
                    edtEmail.requestFocus();
                    return;
                }

                // ===== KIỂM TRA MẬT KHẨU =====
                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Vui lòng nhập mật khẩu");
                    edtPassword.requestFocus();
                    return;
                }

                if (!isValidPassword(password)) {
                    edtPassword.setError(
                            "Mật khẩu phải ≥ 6 ký tự,\n" +
                                    "có ít nhất 1 chữ thường và 1 chữ hoa"
                    );
                    edtPassword.requestFocus();
                    return;
                }

                Toast.makeText(LoginActivity.this,
                        "Đang đăng nhập...", Toast.LENGTH_SHORT).show();

                loginUser(email, password);
            }
        });

        // ================== CHUYỂN SANG ĐĂNG KÝ ==================
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // ================== QUÊN MẬT KHẨU ==================
        tvForgot.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    // ================== KIỂM TRA ĐỊNH DẠNG MẬT KHẨU ==================
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        return password.matches(passwordPattern);
    }

    // ================== XỬ LÝ ĐĂNG NHẬP ==================
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this,
                                    "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            // Chuyển sang màn hình chính
                            Intent intent = new Intent(LoginActivity.this, NavActivity.class);
                            int atIndex = email.indexOf("@");
                            String result = email.substring(0, atIndex);
                            intent.putExtra("Uname", result);
                            startActivity(intent);
                            finish();

                        } else {

                            String message = "Đăng nhập thất bại!";

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

                                    case "ERROR_WRONG_PASSWORD":
                                        message = "Mật khẩu không đúng!";
                                        break;

                                    case "ERROR_USER_DISABLED":
                                        message = "Tài khoản đã bị vô hiệu hóa!";
                                        break;

                                    case "ERROR_TOO_MANY_REQUESTS":
                                        message = "Đăng nhập quá nhiều lần, vui lòng thử lại sau!";
                                        break;

                                    default:
                                        message = "Lỗi đăng nhập, vui lòng thử lại!";
                                        break;
                                }
                            }

                            Toast.makeText(LoginActivity.this,
                                    message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}