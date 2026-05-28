package com.example.crunchyroll_pemvis_5

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.Random

class EditProfileFragment : Fragment() {

    private lateinit var edtProfileName: EditText
    private lateinit var edtProfileUsername: EditText
    private lateinit var btnSaveProfile: TextView
    private lateinit var btnRandomizeUsername: TextView

    private val originalName = MockData.profileName
    private val originalUsername = MockData.profileUsername

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_edit_profile)
        edtProfileName = view.findViewById(R.id.edt_profile_name)
        edtProfileUsername = view.findViewById(R.id.edt_profile_username)
        btnSaveProfile = view.findViewById(R.id.btn_save_profile)
        btnRandomizeUsername = view.findViewById(R.id.btn_randomize_username)

        // Initialize values
        edtProfileName.setText(originalName)
        edtProfileUsername.setText(originalUsername)

        // Set username filter (lowercase, numbers, underscore only)
        edtProfileUsername.filters = arrayOf(InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                val char = source[i]
                if (!char.isLowerCase() && !char.isDigit() && char != '_') {
                    return@InputFilter ""
                }
            }
            null
        })

        // Check for modifications
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        edtProfileName.addTextChangedListener(textWatcher)
        edtProfileUsername.addTextChangedListener(textWatcher)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnRandomizeUsername.setOnClickListener {
            val randomSuffix = Random().nextInt(900) + 100 // 100 to 999
            val baseName = edtProfileName.text.toString().trim()
                .lowercase()
                .replace("\\s+".toRegex(), "_")
                .filter { it.isLowerCase() || it.isDigit() || it == '_' }
            
            val finalBase = if (baseName.isEmpty()) "user" else baseName
            edtProfileUsername.setText("${finalBase}_${randomSuffix}")
        }

        btnSaveProfile.setOnClickListener {
            val name = edtProfileName.text.toString().trim()
            val username = edtProfileUsername.text.toString().trim()

            if (name.isNotEmpty() && username.isNotEmpty()) {
                MockData.profileName = name
                MockData.profileUsername = username
                Toast.makeText(context, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        validateForm()

        return view
    }

    private fun validateForm() {
        val currentName = edtProfileName.text.toString().trim()
        val currentUsername = edtProfileUsername.text.toString().trim()

        val isModified = (currentName != originalName || currentUsername != originalUsername)
        val isNotEmpty = currentName.isNotEmpty() && currentUsername.isNotEmpty()

        if (isModified && isNotEmpty) {
            btnSaveProfile.setBackgroundResource(R.drawable.btn_orange_pill)
            btnSaveProfile.setTextColor(Color.BLACK)
            btnSaveProfile.isClickable = true
            btnSaveProfile.isFocusable = true
        } else {
            btnSaveProfile.setBackgroundResource(R.drawable.btn_disabled_pill)
            btnSaveProfile.setTextColor(Color.parseColor("#5E6267"))
            btnSaveProfile.isClickable = false
            btnSaveProfile.isFocusable = false
        }
    }
}
