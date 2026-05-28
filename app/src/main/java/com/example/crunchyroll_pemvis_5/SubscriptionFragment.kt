package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SubscriptionFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: SubscriptionPlanAdapter

    private lateinit var indicator1: View
    private lateinit var indicator2: View

    private lateinit var btnUpgrade: LinearLayout
    private lateinit var btnCancel: TextView

    // Yearly plans
    private val yearlyPlans = listOf(
        SubscriptionPlan(
            title = "FAN",
            promoTag = "1 TAHUN PROMO",
            oldPrice = "Rp 187.000",
            newPrice = "Rp 140.000/yr for 1 years",
            mascotResId = R.drawable.hime_premium,
            streamingPerangkat = "1",
            hasGameVault = false
        ),
        SubscriptionPlan(
            title = "MEGA FAN",
            promoTag = "1 TAHUN PROMO",
            oldPrice = "Rp 235.000",
            newPrice = "Rp 180.000/yr for 1 years",
            mascotResId = R.drawable.hime_premium,
            streamingPerangkat = "4",
            hasGameVault = true
        )
    )

    // Monthly plans
    private val monthlyPlans = listOf(
        SubscriptionPlan(
            title = "PENGGEMAR",
            promoTag = null,
            oldPrice = null,
            newPrice = "Rp 39.000 / bulan",
            mascotResId = R.drawable.hime_premium,
            streamingPerangkat = "1",
            hasGameVault = false
        ),
        SubscriptionPlan(
            title = "MEGA FAN",
            promoTag = null,
            oldPrice = null,
            newPrice = "Rp 49.000 / bulan",
            mascotResId = R.drawable.hime_premium,
            streamingPerangkat = "4",
            hasGameVault = true
        )
    )

    private var currentPlans = yearlyPlans

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btn_sub_back)
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        tabLayout = view.findViewById(R.id.sub_tab_layout)
        viewPager = view.findViewById(R.id.sub_view_pager)
        indicator1 = view.findViewById(R.id.sub_indicator_1)
        indicator2 = view.findViewById(R.id.sub_indicator_2)
        btnUpgrade = view.findViewById(R.id.btn_upgrade)
        btnCancel = view.findViewById(R.id.btn_cancel_subscription)

        // Set up ViewPager2 peek effect
        viewPager.offscreenPageLimit = 2
        val nextItemVisiblePx = resources.getDimensionPixelOffset(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimensionPixelOffset(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page, position ->
            page.translationX = -pageTranslationX * position
            // Optional zoom effect
            page.scaleY = 1 - (0.05f * Math.abs(position))
        }
        viewPager.setPageTransformer(pageTransformer)

        // Add padding to viewpager for peek edges
        viewPager.setPadding(currentItemHorizontalMarginPx, 0, currentItemHorizontalMarginPx, 0)

        // Initial setup to Yearly tab
        currentPlans = yearlyPlans
        adapter = SubscriptionPlanAdapter(currentPlans)
        viewPager.adapter = adapter

        // Set up manual tabs
        val tabBulanan = tabLayout.newTab()
        val customViewBulanan = layoutInflater.inflate(R.layout.item_tab_subscription, null)
        customViewBulanan.findViewById<TextView>(R.id.tab_title).text = "Bulanan"
        customViewBulanan.findViewById<TextView>(R.id.tab_title).setTextColor(
            ContextCompat.getColor(requireContext(), R.color.tab_unselected_grey)
        )
        tabBulanan.customView = customViewBulanan
        tabLayout.addTab(tabBulanan)

        val tabTahunan = tabLayout.newTab()
        val customViewTahunan = layoutInflater.inflate(R.layout.item_tab_subscription, null)
        val txtTitle = customViewTahunan.findViewById<TextView>(R.id.tab_title)
        txtTitle.text = "Tahunan"
        txtTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        customViewTahunan.findViewById<TextView>(R.id.tab_subtitle).visibility = View.VISIBLE
        tabTahunan.customView = customViewTahunan
        tabLayout.addTab(tabTahunan)

        // Handle page selections
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                updateBottomButtons(tabLayout.selectedTabPosition, position)
            }
        })

        // Tab selection logic
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                context?.let { ctx ->
                    tab?.customView?.findViewById<TextView>(R.id.tab_title)?.setTextColor(
                        ContextCompat.getColor(ctx, R.color.white)
                    )
                }

                if (position == 0) {
                    currentPlans = monthlyPlans
                } else {
                    currentPlans = yearlyPlans
                }
                adapter.updatePlans(currentPlans)
                viewPager.setCurrentItem(0, false)
                updateIndicators(0)
                updateBottomButtons(position, 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                context?.let { ctx ->
                    tab?.customView?.findViewById<TextView>(R.id.tab_title)?.setTextColor(
                        ContextCompat.getColor(ctx, R.color.tab_unselected_grey)
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Default: select the Yearly tab (position 1)
        tabLayout.getTabAt(1)?.select()

        // CTA Click Handlers
        btnUpgrade.setOnClickListener {
            val selectedPlan = currentPlans[viewPager.currentItem]
            val period = if (tabLayout.selectedTabPosition == 0) "Bulanan" else "Tahunan"
            val planNameWithPeriod = "${selectedPlan.title} ($period)"

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, SubscriptionFormFragment.newInstance(planNameWithPeriod))
                ?.addToBackStack(null)
                ?.commit()
        }

        btnCancel.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, SubscriptionFormFragment.newInstance("Gratis"))
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh plans and buttons when returning
        updateBottomButtons(tabLayout.selectedTabPosition, viewPager.currentItem)
    }

    private fun updateIndicators(position: Int) {
        context?.let { ctx ->
            if (position == 0) {
                indicator1.backgroundTintList = null
                indicator2.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.tab_unselected_grey)
            } else {
                indicator1.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.tab_unselected_grey)
                indicator2.backgroundTintList = null
            }
        }
    }

    private fun updateBottomButtons(tabPosition: Int, viewPagerPosition: Int) {
        if (viewPagerPosition >= currentPlans.size) return
        val selectedPlan = currentPlans[viewPagerPosition]
        val isActive = selectedPlan.title.equals(MockData.activeSubscriptionPlan, ignoreCase = true) ||
                (selectedPlan.title.equals("PENGGEMAR", ignoreCase = true) && MockData.activeSubscriptionPlan.equals("FAN", ignoreCase = true)) ||
                (selectedPlan.title.equals("FAN", ignoreCase = true) && MockData.activeSubscriptionPlan.equals("PENGGEMAR", ignoreCase = true))

        if (isActive) {
            btnUpgrade.visibility = View.GONE
            btnCancel.visibility = View.VISIBLE
            btnCancel.text = "Paket Aktif (Batalkan Langganan)"
        } else {
            btnUpgrade.visibility = View.VISIBLE
            btnCancel.visibility = View.GONE
            val txtLabel = btnUpgrade.getChildAt(1) as? TextView
            txtLabel?.text = "Pilih ${selectedPlan.title}"
        }
    }
}
