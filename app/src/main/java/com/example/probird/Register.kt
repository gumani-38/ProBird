package com.example.probird

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    private var isPasswordVisible = false
    private lateinit var edtPassword : EditText
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        edtPassword = findViewById(R.id.edtPassword)
        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_eye, 0)

        edtPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Right drawable index
                if (event.rawX >= (edtPassword.right - edtPassword.compoundDrawables[drawableEnd].bounds.width())) {
                    // Toggle password visibility
                    if (isPasswordVisible) {
                        // Hide password
                        edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_eye, 0)
                    } else {
                        // Show password
                        edtPassword.inputType = InputType.TYPE_CLASS_TEXT
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye, 0)
                    }
                    // Keep cursor at the end
                    edtPassword.setSelection(edtPassword.text.length)
                    isPasswordVisible = !isPasswordVisible

                    // Call performClick for accessibility support
                    edtPassword.performClick()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }
}