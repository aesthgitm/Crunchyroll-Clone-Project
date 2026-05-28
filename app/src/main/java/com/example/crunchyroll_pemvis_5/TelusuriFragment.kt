package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TelusuriFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_telusuri, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Header Buttons click actions
        val btnCast = view.findViewById<ImageView>(R.id.btn_cast)
        val btnSearch = view.findViewById<ImageView>(R.id.btn_search)

        btnCast.setOnClickListener {
            Toast.makeText(context, "Menghubungkan ke perangkat Cast...", Toast.LENGTH_SHORT).show()
        }

        btnSearch.setOnClickListener {
            (activity as? MainActivity)?.openSearchFragment()
        }

        // ViewPager2 Setup
        val viewPager = view.findViewById<ViewPager2>(R.id.telusuri_view_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.telusuri_tab_layout)

        val adapter = TelusuriTabPagerAdapter(requireActivity())
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3

        // Bind TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabView = layoutInflater.inflate(R.layout.item_tab_custom, null)
            val tabText = tabView.findViewById<TextView>(R.id.tab_text)

            context?.let { ctx ->
                when (position) {
                    0 -> {
                        tabText.text = "Semua anime"
                        tabText.setTextColor(ContextCompat.getColor(ctx, R.color.white)) // First active tab
                    }
                    1 -> {
                        tabText.text = "Simulcast"
                        tabText.setTextColor(ContextCompat.getColor(ctx, R.color.tab_unselected_grey))
                    }
                    2 -> {
                        tabText.text = "Genre anime"
                        tabText.setTextColor(ContextCompat.getColor(ctx, R.color.tab_unselected_grey))
                    }
                    3 -> {
                        tabText.text = "Musik"
                        tabText.setTextColor(ContextCompat.getColor(ctx, R.color.tab_unselected_grey))
                    }
                }
            }
            tab.customView = tabView
        }.attach()

        // Tab selection listener to update active tab text colors
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                context?.let { ctx ->
                    tab?.customView?.findViewById<TextView>(R.id.tab_text)?.setTextColor(
                        ContextCompat.getColor(ctx, R.color.white)
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                context?.let { ctx ->
                    tab?.customView?.findViewById<TextView>(R.id.tab_text)?.setTextColor(
                        ContextCompat.getColor(ctx, R.color.tab_unselected_grey)
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
