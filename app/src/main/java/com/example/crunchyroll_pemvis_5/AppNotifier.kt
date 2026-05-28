package com.example.crunchyroll_pemvis_5

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

object AppNotifier {
    fun show(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#1A1C1F"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackbar.show()
    }
}
