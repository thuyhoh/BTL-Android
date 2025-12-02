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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPassword, edtConfirmPass;
    private Button btnRegister;
    private TextView tvSignIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignInLink);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                String confirmPass = edtConfirmPass.getText().toString().trim();

                if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                    Toast.makeText(RegisterActivity.this, "Điền thiếu thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(confirmPass)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Báo người dùng biết là nút đã nhận lệnh
                Toast.makeText(RegisterActivity.this, "Đang xử lý...", Toast.LENGTH_SHORT).show();

                // Bắt đầu đăng ký
                registerUser(email, pass, fullName);
            }
        });

        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser(String email, String password, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // --- ĐOẠN NÀY ĐÃ SỬA ---

                            // 1. Lấy User vừa tạo
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // 2. Gọi hàm lưu Firestore (Chạy ngầm, không cần đợi nó xong)
                                saveUserToFirestore(user.getUid(), fullName, email);
                            }

                            // 3. CHUYỂN TRANG LUÔN (Không đợi Firestore nữa)
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                            FirebaseAuth.getInstance().signOut(); // Đăng xuất

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            // Thêm cờ để xóa lịch sử, không cho back lại trang Register
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(RegisterActivity.this, "Lỗi tạo tài khoản: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String userId, String fullName, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User user = new User(fullName, email, "");

        // Lưu dữ liệu (Code này chạy độc lập)
        db.collection("tài khoản").document(userId).set(user);
        // Chúng ta bỏ qua phần addOnSuccessListener ở đây để tránh bị treo app
    }
}