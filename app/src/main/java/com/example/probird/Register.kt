package com.example.probird

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.MotionEvent
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {
    private var isPasswordVisible = false
    private lateinit var edtPassword: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtContactNumber: EditText
    private lateinit var btnRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Realtime Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtContactNumber = findViewById(R.id.edtPhone)

        btnRegister = findViewById(R.id.btnRegister)



        // Password visibility toggle
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

        // Register button click listener
        btnRegister.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val firstName = edtFirstName.text.toString().trim()
            val lastName = edtLastName.text.toString().trim()
            val contactNumber = edtContactNumber.text.toString().trim()
            val maxDistance = "25"
            val metric = "km"
            if (validateInputs(email, password, firstName, lastName, contactNumber)) {
                registerUser(email, password, firstName, lastName, contactNumber, maxDistance, metric)
            }
        }
    }

    // Validate the registration form inputs
    private fun validateInputs(email: String, password: String, firstName: String, lastName: String, contactNumber: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Please enter a valid email"
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            edtPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (firstName.isEmpty()) {
            edtFirstName.error = "Please enter your first name"
            return false
        }
        if (lastName.isEmpty()) {
            edtLastName.error = "Please enter your last name"
            return false
        }
        if (contactNumber.isEmpty()) {
            edtContactNumber.error = "Please enter your contact number"
            return false
        }
        return true
    }

    // Register user in Firebase Authentication and store additional details in Realtime Database
    private fun registerUser(email: String, password: String, firstName: String, lastName: String, contactNumber: String, maxDistance: String, metric: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    saveUserDetails(it, firstName, lastName, contactNumber, maxDistance, metric)
                }
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save user details in Firebase Realtime Database
    private fun saveUserDetails(user: FirebaseUser, firstName: String, lastName: String, contactNumber: String, maxDistance: String, metric: String) {
        val userId = user.uid
        val userMap = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "contactNumber" to contactNumber,
            "maxDistance" to maxDistance,
            "metric" to metric
        )
        database.child("users").child(userId).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "User details saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save user details: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Toggle password visibility
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_eye, 0)
        } else {
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT
            edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.open_eye, 0)
        }
        edtPassword.setSelection(edtPassword.text.length)
        isPasswordVisible = !isPasswordVisible
        edtPassword.performClick()
    }
}
