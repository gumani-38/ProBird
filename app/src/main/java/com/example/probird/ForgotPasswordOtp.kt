package com.example.probird

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgotPasswordOtp : AppCompatActivity() {
    private lateinit var btnBackward : ImageView
    private  lateinit var btnSubmit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password_otp)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnBackward = findViewById(R.id.btnBack)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnBackward.setOnClickListener{
            val intent = Intent(this@ForgotPasswordOtp, ForgotPassword::class.java)
            startActivity(intent)
        }
        btnSubmit.setOnClickListener {
            val intent = Intent(this@ForgotPasswordOtp, ResetPassword::class.java)
            startActivity(intent)
        }
    }
}