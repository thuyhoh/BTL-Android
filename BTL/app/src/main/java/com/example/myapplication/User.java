package com.example.myapplication;

public class User {
    private String fullName;
    private String email;
    private String phone; // Ví dụ thêm số điện thoại

    // 1. Bắt buộc phải có Constructor rỗng cho Firebase
    public User() { }

    // 2. Constructor đầy đủ
    public User(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // 3. Getter và Setter (Bôi đen code -> Chuột phải -> Generate -> Getter and Setter)
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}