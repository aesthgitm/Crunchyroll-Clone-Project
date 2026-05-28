package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SubtitleLanguageFragment : Fragment() {

    private val languages = listOf(
        "Bahasa Indonesia",
        "English"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_subtitle_language, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_subtitle_lang)
        val containerOptions = view.findViewById<LinearLayout>(R.id.container_subtitle_options)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val marginPx = (16 * resources.displayMetrics.density).toInt()
        val paddingPx = (16 * resources.displayMetrics.density).toInt()

        // Resolve selectableItemBackground dengan benar menggunakan TypedValue
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
        val selectableResId = typedValue.resourceId

        for (lang in languages) {
            val row = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(marginPx, paddingPx, marginPx, paddingPx)
                isClickable = true
                isFocusable = true
                setBackgroundResource(selectableResId)
            }

            val radioButton = RadioButton(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                buttonTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#FF6400")
                )
                isChecked = (MockData.subtitleLanguage == lang)
                isClickable = false
                isFocusable = false
            }

            val textView = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(marginPx, 0, 0, 0)
                }
                text = lang
                setTextColor(resources.getColor(R.color.white, null))
                textSize = 15f
                typeface = androidx.core.content.res.ResourcesCompat.getFont(
                    requireContext(), R.font.poppinsregular
                )
            }

            row.addView(radioButton)
            row.addView(textView)

            row.setOnClickListener {
                MockData.subtitleLanguage = lang
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (userId.isNotEmpty()) {
                    FirestoreHelper().saveCurrentUserSnapshot(userId) { success ->
                        activity?.runOnUiThread {
                            val msg = if (success) {
                                "Bahasa takarir diatur ke: $lang"
                            } else {
                                "Bahasa takarir diperbarui lokal, gagal sinkron cloud"
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            activity?.supportFragmentManager?.popBackStack()
                        }
                    }
                } else {
                    Toast.makeText(context, "Bahasa takarir diatur ke: $lang", Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager?.popBackStack()
                }
            }

            containerOptions.addView(row)

            // Divider
            val divider = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
                )
                setBackgroundColor(android.graphics.Color.parseColor("#22FFFFFF"))
            }
            containerOptions.addView(divider)
        }

        return view
    }
}
