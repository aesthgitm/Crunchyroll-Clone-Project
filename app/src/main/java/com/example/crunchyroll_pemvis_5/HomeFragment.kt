package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment() {

    private lateinit var sectionContinue: LinearLayout
    private lateinit var rvContinue: RecyclerView
    private lateinit var trendingAdapter: AnimePosterAdapter
    private lateinit var exclusiveAdapter: AnimePosterAdapter
    private lateinit var bannerAdapter: HomeBannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val rvTrending = view.findViewById<RecyclerView>(R.id.rv_trending)
        val rvExclusive = view.findViewById<RecyclerView>(R.id.rv_exclusive)
        val btnSearch = view.findViewById<ImageView>(R.id.btn_home_search)
        val bannerViewPager = view.findViewById<ViewPager2>(R.id.banner_view_pager)

        sectionContinue = view.findViewById(R.id.section_continue_watching)
        rvContinue = view.findViewById(R.id.rv_continue)

        // Set layouts to horizontal scroll orientation
        rvTrending.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvExclusive.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvContinue.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Assign Adapters with dynamic callback triggers
        trendingAdapter = AnimePosterAdapter(MockData.trendingAnime) { anime ->
            (activity as? MainActivity)?.openDetailFragment(anime)
        }
        exclusiveAdapter = AnimePosterAdapter(MockData.exclusiveAnime) { anime ->
            (activity as? MainActivity)?.openDetailFragment(anime)
        }

        rvTrending.adapter = trendingAdapter
        rvExclusive.adapter = exclusiveAdapter

        // Setup ViewPager2 for swiping banners
        bannerAdapter = HomeBannerAdapter(
            MockData.bannerAnime,
            onPlayClick = { anime ->
                (activity as? MainActivity)?.openDetailFragment(anime)
            },
            onItemClick = { anime ->
                (activity as? MainActivity)?.openDetailFragment(anime)
            }
        )
        bannerViewPager.adapter = bannerAdapter

        // Setup page indicator dot updates
        val indicators = listOf<View>(
            view.findViewById(R.id.indicator_1),
            view.findViewById(R.id.indicator_2),
            view.findViewById(R.id.indicator_3),
            view.findViewById(R.id.indicator_4),
            view.findViewById(R.id.indicator_5)
        )

        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in indicators.indices) {
                    if (i == position) {
                        indicators[i].setBackgroundColor(android.graphics.Color.parseColor("#FF6400"))
                    } else {
                        indicators[i].setBackgroundColor(android.graphics.Color.parseColor("#55FFFFFF"))
                    }
                }
            }
        })

        // Search action trigger
        btnSearch.setOnClickListener {
            (activity as? MainActivity)?.openSearchFragment()
        }

        // See all triggers redirecting to the search page for browsing
        view.findViewById<TextView>(R.id.btn_see_all).setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.navigation_telusuri)
        }

        updateContinueWatching()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateContinueWatching()
    }

    private fun updateContinueWatching() {
        if (::sectionContinue.isInitialized) {
            if (MockData.watchHistory.isNotEmpty()) {
                sectionContinue.visibility = View.VISIBLE
                rvContinue.adapter = AnimePosterAdapter(MockData.watchHistory) { anime ->
                    (activity as? MainActivity)?.openDetailFragment(anime)
                }
            } else {
                sectionContinue.visibility = View.GONE
            }
        }
    }
}
