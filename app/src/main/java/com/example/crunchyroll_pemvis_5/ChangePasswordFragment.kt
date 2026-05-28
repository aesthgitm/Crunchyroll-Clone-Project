package com.example.crunchyroll_pemvis_5

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ChangePasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_change_password)
        val edtNewPassword = view.findViewById<EditText>(R.id.edt_new_password)
        val edtConfirmPassword = view.findViewById<EditText>(R.id.edt_confirm_password)
        val btnReset = view.findViewById<TextView>(R.id.btn_reset_password)
        val btnTerms = view.findViewById<View>(R.id.btn_terms)
        val btnPrivacy = view.findViewById<View>(R.id.btn_privacy_pw)
        val btnCookie = view.findViewById<View>(R.id.btn_cookie)
        val btnLanguage = view.findViewById<View>(R.id.btn_language_selector)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pw = edtNewPassword.text.toString()
                val confirm = edtConfirmPassword.text.toString()
                val isValid = pw.length >= 6 && confirm.isNotEmpty()
                if (isValid) {
                    btnReset.setTextColor(Color.WHITE)
                } else {
                    btnReset.setTextColor(Color.parseColor("#5E6267"))
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        edtNewPassword.addTextChangedListener(textWatcher)
        edtConfirmPassword.addTextChangedListener(textWatcher)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnReset.setOnClickListener {
            val pw = edtNewPassword.text.toString()
            val confirm = edtConfirmPassword.text.toString()
            when {
                pw.length < 6 -> Toast.makeText(context, "Kata sandi minimal 6 karakter", Toast.LENGTH_SHORT).show()
                pw != confirm -> Toast.makeText(context, "Kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
                else -> {
                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        currentUser.updatePassword(pw).addOnCompleteListener { task ->
                            MockData.userPassword = pw
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Kata sandi berhasil diubah!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Kata sandi diperbarui lokal, gagal sinkron auth", Toast.LENGTH_SHORT).show()
                            }
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    } else {
                        MockData.userPassword = pw
                        Toast.makeText(context, "Kata sandi berhasil diubah!", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        }

        btnTerms.setOnClickListener {
            Toast.makeText(context, "Membuka Ketentuan Penggunaan...", Toast.LENGTH_SHORT).show()
        }
        btnPrivacy.setOnClickListener {
            Toast.makeText(context, "Membuka Kebijakan Privasi...", Toast.LENGTH_SHORT).show()
        }
        btnCookie.setOnClickListener {
            Toast.makeText(context, "Membuka Alat Persetujuan Cookie...", Toast.LENGTH_SHORT).show()
        }
        btnLanguage.setOnClickListener {
            Toast.makeText(context, "Membuka pilihan bahasa...", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
