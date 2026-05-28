package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SubscriptionFormFragment : Fragment() {

    private var planName: String = ""

    companion object {
        private const val ARG_PLAN_NAME = "plan_name"

        fun newInstance(planName: String): SubscriptionFormFragment {
            val fragment = SubscriptionFormFragment()
            val args = Bundle()
            args.putString(ARG_PLAN_NAME, planName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        planName = arguments?.getString(ARG_PLAN_NAME) ?: "MEGA FAN"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btn_form_back)
        val txtPlanHeader = view.findViewById<TextView>(R.id.txt_plan_header)
        val edtName = view.findViewById<EditText>(R.id.edt_form_name)
        val edtEmail = view.findViewById<EditText>(R.id.edt_form_email)
        val edtPassword = view.findViewById<EditText>(R.id.edt_form_password)
        val btnSubmit = view.findViewById<Button>(R.id.btn_form_submit)

        txtPlanHeader.text = "Langganan: $planName"

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        edtName.setText(MockData.profileName)
        edtEmail.setText(MockData.loggedInEmail)
        edtPassword.setText(MockData.userPassword)

        val checkFields = {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            val isFilled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            btnSubmit.isEnabled = isFilled

            context?.let { ctx ->
                if (isFilled) {
                    btnSubmit.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.btn_orange_pill))
                    btnSubmit.setTextColor(ContextCompat.getColor(ctx, R.color.black))
                } else {
                    btnSubmit.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.btn_disabled_pill))
                    btnSubmit.setTextColor(ContextCompat.getColor(ctx, R.color.grey_text_disabled))
                }
            }
        }

        // Jalankan pengecekan pertama kali
        checkFields()

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        edtName.addTextChangedListener(watcher)
        edtEmail.addTextChangedListener(watcher)
        edtPassword.addTextChangedListener(watcher)

        btnSubmit.setOnClickListener {
            val normalizedPlan = when {
                planName.equals("Gratis", ignoreCase = true) -> "Gratis"
                planName.contains("MEGA FAN", ignoreCase = true) -> "MEGA FAN"
                planName.contains("FAN", ignoreCase = true) || planName.contains("PENGGEMAR", ignoreCase = true) -> "FAN"
                else -> planName
            }
            MockData.activeSubscriptionPlan = normalizedPlan
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (userId.isNotEmpty()) {
                val firestorePlan = if (normalizedPlan.equals("Gratis", ignoreCase = true)) "Free" else normalizedPlan
                FirestoreHelper().updateSubscription(userId, firestorePlan) { success ->
                    if (success) {
                        Toast.makeText(context, "Langganan $normalizedPlan berhasil disimpan online!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Langganan $normalizedPlan diaktifkan lokal (gagal online)", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Langganan $normalizedPlan berhasil diaktifkan secara lokal!", Toast.LENGTH_LONG).show()
            }

            // Kembali ke layar sebelumnya secara aman
            activity?.supportFragmentManager?.let { fm ->
                fm.popBackStack()
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                }
            }
        }
    }
}
