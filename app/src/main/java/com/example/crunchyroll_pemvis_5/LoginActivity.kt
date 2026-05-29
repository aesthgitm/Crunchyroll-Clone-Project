package com.example.crunchyroll_pemvis_5

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
// import android.widget.Toast // Removed in favor of AppNotifier
import com.example.crunchyroll_pemvis_5.AppNotifier // Optional import, same package so can be omitted
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnBackLoginListener()
        btnRegisterListener()
        setupLoginButton()
    }

    @SuppressLint("WrongViewCast")
    private fun btnBackLoginListener() {
        val closeicon = findViewById<ImageView>(R.id.closeicon)
        closeicon.setOnClickListener {
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun btnRegisterListener() {
        val textbuatakun = findViewById<TextView>(R.id.textbuatakun)
        textbuatakun.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLoginButton() {
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        val etEmail = findViewById<EditText>(R.id.textemail)
        val etPassword = findViewById<EditText>(R.id.password)

        // Mulai dalam keadaan disabled
        btnlogin.isEnabled = false
        btnlogin.background = ContextCompat.getDrawable(this, R.drawable.btn_disabled_pill)
        btnlogin.setTextColor(ContextCompat.getColor(this, R.color.grey_text_disabled))

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val emailFilled = etEmail.text.toString().trim().isNotEmpty()
                val passwordFilled = etPassword.text.toString().trim().isNotEmpty()
                if (emailFilled && passwordFilled) {
                    btnlogin.isEnabled = true
                    btnlogin.background = ContextCompat.getDrawable(this@LoginActivity, R.drawable.btn_orange_pill)
                    btnlogin.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.white))
                } else {
                    btnlogin.isEnabled = false
                    btnlogin.background = ContextCompat.getDrawable(this@LoginActivity, R.drawable.btn_disabled_pill)
                    btnlogin.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.grey_text_disabled))
                }
            }
        }

        etEmail.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)

        btnlogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            btnlogin.isEnabled = false
            
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        val uid = firebaseUser?.uid ?: ""
                        
                        FirestoreHelper().getUserProfile(uid,
                            onSuccess = { user ->
                                if (user != null) {
                                    MockData.loggedInEmail = user.email
                                    MockData.profileUsername = user.username
                                    MockData.profileName = user.profileName
                                    MockData.userPassword = password
                                    MockData.activeSubscriptionPlan = if (user.membershipType == "Free") "Gratis" else user.membershipType
                                    MockData.activeContentRestriction = user.contentRatingRestriction
                                    MockData.audioLanguage = user.audioLanguage
                                    MockData.subtitleLanguage = user.subtitleLanguage
                                    MockData.audioDescriptionEnabled = user.audioDescriptionEnabled
                                    MockData.downloadQuality = user.downloadQuality
                                    MockData.streamCellularEnabled = user.streamCellularEnabled
                                    MockData.downloadCellularEnabled = user.downloadCellularEnabled
                                } else {
                                    MockData.loggedInEmail = email
                                    MockData.profileUsername = if (email.contains("@")) email.substringBefore("@") else "User"
                                    MockData.profileName = MockData.profileUsername
                                    MockData.userPassword = password
                                }
                                AppNotifier.show(this, "Berhasil masuk!")
                                NotificationHelper.showSystemNotification(this, "Selamat Datang!", "Berhasil masuk sebagai ${MockData.profileUsername}")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            onFailure = {
                                MockData.loggedInEmail = email
                                MockData.profileUsername = if (email.contains("@")) email.substringBefore("@") else "User"
                                MockData.profileName = MockData.profileUsername
                                MockData.userPassword = password
                                
                                AppNotifier.show(this, "Masuk berhasil!")
                                NotificationHelper.showSystemNotification(this, "Selamat Datang!", "Berhasil masuk sebagai ${MockData.profileUsername} (Offline)")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        )
                    } else {
                        btnlogin.isEnabled = true
                        val errorMsg = task.exception?.localizedMessage ?: "Masuk gagal"
                        AppNotifier.show(this, "Error: $errorMsg")
                    }
                }
        }
    }
}
