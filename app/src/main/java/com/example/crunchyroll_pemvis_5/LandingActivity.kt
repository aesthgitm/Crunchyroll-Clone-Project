package com.example.crunchyroll_pemvis_5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            FirestoreHelper().getUserProfile(uid,
                onSuccess = { user ->
                    if (user != null) {
                        MockData.loggedInEmail = user.email
                        MockData.profileUsername = user.username
                        MockData.profileName = user.profileName
                        MockData.activeSubscriptionPlan = if (user.membershipType == "Free") "Gratis" else user.membershipType
                        MockData.activeContentRestriction = user.contentRatingRestriction
                        MockData.audioLanguage = user.audioLanguage
                        MockData.subtitleLanguage = user.subtitleLanguage
                        MockData.audioDescriptionEnabled = user.audioDescriptionEnabled
                        MockData.downloadQuality = user.downloadQuality
                        MockData.streamCellularEnabled = user.streamCellularEnabled
                        MockData.downloadCellularEnabled = user.downloadCellularEnabled
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_landing)
        supportActionBar?.hide()
        btnLoginListener()
    }

    private fun btnLoginListener() {
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        btnlogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val btnjelajahiujicoba = findViewById<Button>(R.id.btnjelajahiujicoba)
        btnjelajahiujicoba.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
