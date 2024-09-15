package com.example.probird

import android.app.Dialog
import android.os.Bundle
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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
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

        return view
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
                Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
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

        val passwordInput = bottomSheetView.findViewById<EditText>(R.id.edtContactNumber)
        val updateButton = bottomSheetView.findViewById<Button>(R.id.btnUpdate)

        updateButton.setOnClickListener {
            val password = passwordInput.text.toString()
            if (password.isNotEmpty()) {
                Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a password", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog.show()
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
