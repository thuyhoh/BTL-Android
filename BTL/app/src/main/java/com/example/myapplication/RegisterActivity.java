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

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignInLink);

        // ================== NÚT ĐĂNG KÝ ==================
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                String confirmPass = edtConfirmPass.getText().toString().trim();

                // ===== KIỂM TRA RỖNG =====
                if (TextUtils.isEmpty(fullName)) {
                    edtFullName.setError("Vui lòng nhập họ tên");
                    edtFullName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Vui lòng nhập Email");
                    edtEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    edtPassword.setError("Vui lòng nhập mật khẩu");
                    edtPassword.requestFocus();
                    return;
                }

                // ===== KIỂM TRA ĐỊNH DẠNG MẬT KHẨU =====
                if (!isValidPassword(pass)) {
                    edtPassword.setError(
                            "Mật khẩu phải từ 6 ký tự trở lên\n" +
                                    "Có ít nhất 1 chữ thường và 1 chữ hoa"
                    );
                    edtPassword.requestFocus();
                    return;
                }

                // ===== KIỂM TRA XÁC NHẬN MẬT KHẨU =====
                if (!pass.equals(confirmPass)) {
                    edtConfirmPass.setError("Mật khẩu không khớp");
                    edtConfirmPass.requestFocus();
                    return;
                }

                Toast.makeText(RegisterActivity.this,
                        "Đang tạo tài khoản...", Toast.LENGTH_SHORT).show();

                registerUser(email, pass, fullName);
            }
        });

        // ================== CHUYỂN SANG LOGIN ==================
        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    // ================== ĐĂNG KÝ FIREBASE ==================
    private void registerUser(String email, String password, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                saveUserToFirestore(user.getUid(), fullName, email);
                            }

                            Toast.makeText(RegisterActivity.this,
                                    "Đăng ký thành công! Vui lòng đăng nhập.",
                                    Toast.LENGTH_LONG).show();

                            FirebaseAuth.getInstance().signOut();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {

                            String message = "Đăng ký thất bại!";

                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException)
                                        task.getException()).getErrorCode();

                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        message = "Email không đúng định dạng!";
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        message = "Email đã được đăng ký!";
                                        break;

                                    case "ERROR_WEAK_PASSWORD":
                                        message = "Mật khẩu quá yếu!";
                                        break;

                                    case "ERROR_TOO_MANY_REQUESTS":
                                        message = "Thao tác quá nhiều lần, vui lòng thử lại sau!";
                                        break;

                                    default:
                                        message = "Lỗi hệ thống, vui lòng thử lại!";
                                        break;
                                }
                            }

                            Toast.makeText(RegisterActivity.this,
                                    message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // ================== LƯU FIRESTORE ==================
    private void saveUserToFirestore(String userId, String fullName, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        User user = new User(fullName, email, "");

        db.collection("tai_khoan")
                .document(userId)
                .set(user);
    }

    // ================== KIỂM TRA MẬT KHẨU ==================
    private boolean isValidPassword(String password) {
        // ≥ 6 ký tự, có ít nhất 1 chữ thường và 1 chữ hoa
        String pattern = "^(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        return password.matches(pattern);
    }
}
