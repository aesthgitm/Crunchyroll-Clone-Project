package com.example.crunchyroll_pemvis_5

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

object AppNotifier {
    fun show(view: View, message: String) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view as Snackbar.SnackbarLayout
        
        // Remove standard snackbar styling
        snackbarView.setBackgroundColor(Color.TRANSPARENT)
        snackbarView.setPadding(0, 0, 0, 0)
        snackbarView.elevation = 12f
        
        // Inflate our high-fidelity premium custom layout
        val inflater = LayoutInflater.from(view.context)
        val customView = inflater.inflate(R.layout.custom_toast, null)
        
        val text: TextView = customView.findViewById(R.id.toast_message)
        text.text = message
        
        val icon: ImageView = customView.findViewById(R.id.toast_icon)
        // Select custom premium icons dynamically based on keywords
        val iconRes = when {
            message.contains("unduh", ignoreCase = true) || message.contains("download", ignoreCase = true) -> 
                android.R.drawable.stat_sys_download_done
            message.contains("hapus", ignoreCase = true) || message.contains("batal", ignoreCase = true) -> 
                android.R.drawable.ic_menu_delete
            message.contains("simpan", ignoreCase = true) || message.contains("tambah", ignoreCase = true) || message.contains("daftar", ignoreCase = true) -> 
                android.R.drawable.star_big_on
            else -> 
                android.R.drawable.ic_dialog_info
        }
        icon.setImageResource(iconRes)
        
        // Add to snackbar layout
        snackbarView.removeAllViews()
        snackbarView.addView(customView)
        
        // Float beautifully above the system bars & bottom navigation
        val params = snackbarView.layoutParams as android.view.ViewGroup.MarginLayoutParams
        params.setMargins(64, 0, 64, 180)
        snackbarView.layoutParams = params
        
        // Micro-animation entry
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        
        // Fire system status bar notification for essential milestones
        try {
            val ctx = view.context
            if (message.contains("berhasil", ignoreCase = true) || 
                message.contains("sukses", ignoreCase = true) || 
                message.contains("simpan", ignoreCase = true)) {
                NotificationHelper.showSystemNotification(ctx, "Aktivitas Akun", message)
            }
        } catch (e: Exception) {}
        
        snackbar.show()
    }
}
