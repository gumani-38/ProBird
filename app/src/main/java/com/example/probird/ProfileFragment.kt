package com.example.probird

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var metric: TextView
    private lateinit var phone: TextView
    private lateinit var maxDistance: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
         name = view.findViewById(R.id.name)
         email = view.findViewById(R.id.email)
        metric = view.findViewById(R.id.metric)
        phone = view.findViewById(R.id.contactNumber)
        maxDistance = view.findViewById(R.id.tvDistance)
        val passwordLayout = view.findViewById<LinearLayout>(R.id.passwordLayout)

        passwordLayout.setOnClickListener {
            showBottomSheetDialog()
        }
        val contactNumberLayout = view.findViewById<LinearLayout>(R.id.contactNumberLayout)

        contactNumberLayout.setOnClickListener {
            showContactBottomSheetDialog()
        }
        val tvDistance = view.findViewById<TextView>(R.id.tvDistance)

        tvDistance.setOnClickListener {
            showUpdateDistanceDialog()
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        // Fetch user data
        fetchUserData()
        return view
    }
    private fun fetchUserData() {
        // Get the current user ID
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val firstName = snapshot.child("firstName").getValue(String::class.java)
                        val lastName = snapshot.child("lastName").getValue(String::class.java)
                        val emailValue = snapshot.child("email").getValue(String::class.java)
                        val metricValue = snapshot.child("metric").getValue(String::class.java)
                        val contactNumber = snapshot.child("contactNumber").getValue(String::class.java)
                        val maxDistanceValue = snapshot.child("maxDistance").getValue(Int::class.java)
                        Log.v("user : ",snapshot.toString())

                        // Update UI elements with retrieved data
                        name.text = "$firstName $lastName"
                        email.text = emailValue
                        metric.text = metricValue
                        phone.text = contactNumber
                        maxDistance.text = "$maxDistanceValue km"
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.password_update_bottom_sheet, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val passwordInput = bottomSheetView.findViewById<EditText>(R.id.edtPassword)
        val updateButton = bottomSheetView.findViewById<Button>(R.id.btnUpdatePassword)

        updateButton.setOnClickListener {
            val password = passwordInput.text.toString()
            if (password.isNotEmpty()) {
                updatePassword(password)  // Update password in Firebase
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a password", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog.show()
    }

    private fun showContactBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.update_contact_number_bottom_sheet, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val contactNumberInput = bottomSheetView.findViewById<EditText>(R.id.edtContactNumber)
        val updateButton = bottomSheetView.findViewById<Button>(R.id.btnUpdate)

        updateButton.setOnClickListener {
            val contactNumber = contactNumberInput.text.toString()
            if (contactNumber.isNotEmpty()) {
                updateContactNumber(contactNumber)  // Update contact number in Firebase
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a contact number", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog.show()
    }

    private fun updatePassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error updating password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateContactNumber(newContactNumber: String) {
        val userId = auth.currentUser?.uid ?: return
        databaseReference.child(userId).child("contactNumber").setValue(newContactNumber)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Contact number updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error updating contact number", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showUpdateDistanceDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.update_travel_distance_popup_box)

        val edtDistance = dialog.findViewById<EditText>(R.id.edtDistance)
        val btnUpdateDistance = dialog.findViewById<Button>(R.id.btnUpdateDistance)
        val closePopup = dialog.findViewById<ImageView>(R.id.closePopup)

        closePopup.setOnClickListener {
            dialog.dismiss()
        }

        btnUpdateDistance.setOnClickListener {
            val distance = edtDistance.text.toString()
            if (distance.isNotEmpty()) {
                Toast.makeText(requireContext(), "Distance updated to $distance km", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid distance", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
