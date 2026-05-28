package com.example.crunchyroll_pemvis_5

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnBackLoginListener()
        btnLoginListener()
        setupRegisterButton()
    }

    @SuppressLint("WrongViewCast")
    private fun btnBackLoginListener() {
        val closeicon = findViewById<ImageView>(R.id.closeicon)
        closeicon.setOnClickListener {
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun btnLoginListener() {
        val textmasuk = findViewById<TextView>(R.id.textmasuk)
        textmasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRegisterButton() {
        val btnbuatakun = findViewById<Button>(R.id.btnbuatakun)
        val etEmail = findViewById<EditText>(R.id.textemail)
        val etPassword = findViewById<EditText>(R.id.password)

        // Mulai dalam keadaan disabled
        btnbuatakun.isEnabled = false
        btnbuatakun.background = ContextCompat.getDrawable(this, R.drawable.btn_disabled_pill)
        btnbuatakun.setTextColor(ContextCompat.getColor(this, R.color.grey_text_disabled))

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val emailFilled = etEmail.text.toString().trim().isNotEmpty()
                val passwordFilled = etPassword.text.toString().trim().isNotEmpty()
                if (emailFilled && passwordFilled) {
                    btnbuatakun.isEnabled = true
                    btnbuatakun.background = ContextCompat.getDrawable(this@RegisterActivity, R.drawable.btn_orange_pill)
                    btnbuatakun.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.white))
                } else {
                    btnbuatakun.isEnabled = false
                    btnbuatakun.background = ContextCompat.getDrawable(this@RegisterActivity, R.drawable.btn_disabled_pill)
                    btnbuatakun.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.grey_text_disabled))
                }
            }
        }

        etEmail.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)

        btnbuatakun.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            btnbuatakun.isEnabled = false
            
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        val uid = firebaseUser?.uid ?: ""
                        val username = if (email.contains("@")) email.substringBefore("@") else "User"
                        
                        val newUser = User(
                            uid = uid,
                            username = username,
                            email = email,
                            profileName = username,
                            membershipType = "Free",
                            contentRatingRestriction = "16+",
                            audioLanguage = "Bahasa Indonesia",
                            subtitleLanguage = "English",
                            audioDescriptionEnabled = false,
                            downloadQuality = 0,
                            streamCellularEnabled = false,
                            downloadCellularEnabled = false
                        )
                        
                        FirestoreHelper().saveUser(newUser) { success ->
                            if (success) {
                                MockData.loggedInEmail = email
                                MockData.profileUsername = username
                                MockData.profileName = username
                                MockData.userPassword = password
                                MockData.activeSubscriptionPlan = "Gratis"
                                
                                Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                btnbuatakun.isEnabled = true
                                Toast.makeText(this, "Gagal menyimpan data profil ke database.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        btnbuatakun.isEnabled = true
                        val errorMsg = task.exception?.localizedMessage ?: "Pendaftaran gagal"
                        Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
