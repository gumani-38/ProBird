package com.example.probird

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button
    private lateinit var txtLinkRegister: TextView
    private lateinit var txtLinkForgotP: TextView
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private var isPasswordVisible = false
    private lateinit var auth: FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // View elements
        btnLogin = findViewById(R.id.btnLogin)
        txtLinkRegister = findViewById(R.id.txtLink)
        txtLinkForgotP = findViewById(R.id.txtForgotPasswordLink)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up listeners
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            // Validate input
            if (email.isEmpty()) {
                edtEmail.error = "Email is required"
                edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "Please provide a valid email"
                edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                edtPassword.error = "Password is required"
                edtPassword.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                edtPassword.error = "Password must be at least 6 characters"
                edtPassword.requestFocus()
                return@setOnClickListener
            }

            // Perform login with Firebase Authentication
            loginUser(email, password)
        }

        txtLinkRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, Register::class.java)
            startActivity(intent)
        }

        txtLinkForgotP.setOnClickListener {
            val intent = Intent(this@MainActivity, ForgotPassword::class.java)
            startActivity(intent)
        }

        // Handle password visibility toggle
        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_eye, 0)
        edtPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // Right drawable index
                if (event.rawX >= (edtPassword.right - edtPassword.compoundDrawables[drawableEnd].bounds.width())) {
                    togglePasswordVisibility()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    // Function to toggle password visibility
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_eye, 0)
        } else {
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT
            edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye, 0)
        }
        // Keep cursor at the end
        edtPassword.setSelection(edtPassword.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    // Function to login user using Firebase Authentication
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    if (user != null) {
                        // Redirect to home screen
                        val intent = Intent(this@MainActivity, Home::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@MainActivity, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }

    }
}
