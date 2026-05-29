package com.example.crunchyroll_pemvis_5

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object AppNotifier {

    /**
     * Premium Snackbar notification — use from Fragments where you have a root View.
     */
    fun show(view: View, message: String) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view as Snackbar.SnackbarLayout
        
        snackbarView.setBackgroundColor(Color.TRANSPARENT)
        snackbarView.setPadding(0, 0, 0, 0)
        snackbarView.elevation = 12f
        
        val inflater = LayoutInflater.from(view.context)
        val customView = inflater.inflate(R.layout.custom_toast, null)
        
        val text: TextView = customView.findViewById(R.id.toast_message)
        text.text = message
        
        val icon: ImageView = customView.findViewById(R.id.toast_icon)
        icon.setImageResource(pickIcon(message))
        
        snackbarView.removeAllViews()
        snackbarView.addView(customView)
        
        val params = snackbarView.layoutParams as android.view.ViewGroup.MarginLayoutParams
        params.setMargins(64, 0, 64, 180)
        snackbarView.layoutParams = params
        
        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackbar.show()
    }

    /**
     * Premium Snackbar notification — use from Activities (LoginActivity, RegisterActivity, etc.)
     */
    fun show(activity: Activity, message: String) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        if (rootView != null) {
            show(rootView, message)
        } else {
            showToast(activity, message)
        }
    }

    /**
     * Premium custom Toast — use from Adapters, ViewHolders, or anywhere only a Context is available.
     */
    fun showToast(context: Context, message: String) {
        try {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.custom_toast, null)

            val text: TextView = layout.findViewById(R.id.toast_message)
            text.text = message

            val icon: ImageView = layout.findViewById(R.id.toast_icon)
            icon.setImageResource(pickIcon(message))

            @Suppress("DEPRECATION")
            val toast = Toast(context.applicationContext)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 200)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
        } catch (e: Exception) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Smart icon picker based on message keywords.
     */
    private fun pickIcon(message: String): Int {
        return when {
            message.contains("unduh", ignoreCase = true) || message.contains("download", ignoreCase = true) ->
                android.R.drawable.stat_sys_download_done
            message.contains("hapus", ignoreCase = true) || message.contains("batal", ignoreCase = true) ->
                android.R.drawable.ic_menu_delete
            message.contains("berhasil", ignoreCase = true) || message.contains("sukses", ignoreCase = true) ->
                android.R.drawable.ic_dialog_info
            message.contains("simpan", ignoreCase = true) || message.contains("tambah", ignoreCase = true) || message.contains("daftar", ignoreCase = true) ->
                android.R.drawable.star_big_on
            message.contains("gagal", ignoreCase = true) || message.contains("error", ignoreCase = true) ->
                android.R.drawable.ic_dialog_alert
            message.contains("putar", ignoreCase = true) || message.contains("memutar", ignoreCase = true) ->
                android.R.drawable.ic_media_play
            message.contains("bagikan", ignoreCase = true) || message.contains("membagikan", ignoreCase = true) ->
                android.R.drawable.ic_menu_share
            message.contains("pengaturan", ignoreCase = true) || message.contains("diatur", ignoreCase = true) || message.contains("diubah", ignoreCase = true) ->
                android.R.drawable.ic_menu_preferences
            else ->
                android.R.drawable.ic_dialog_info
        }
    }
}
