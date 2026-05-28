package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TabContentFragment : Fragment() {

    private var tabIndex = 0
    private lateinit var centerContainer: LinearLayout
    private lateinit var actionButton: Button
    private lateinit var rvPopulatedContent: RecyclerView
    
    private lateinit var illustration: ImageView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var premiumBtnContainer: LinearLayout
    private lateinit var txtPremiumBtn: TextView

    companion object {
        private const val ARG_TAB_INDEX = "tab_index"

        fun newInstance(tabIndex: Int): TabContentFragment {
            val fragment = TabContentFragment()
            val args = Bundle()
            args.putInt(ARG_TAB_INDEX, tabIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabIndex = arguments?.getInt(ARG_TAB_INDEX) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_content, container, false)

        illustration = view.findViewById(R.id.illustration)
        titleText = view.findViewById(R.id.title_text)
        subtitleText = view.findViewById(R.id.subtitle_text)
        actionButton = view.findViewById(R.id.action_button)
        premiumBtnContainer = view.findViewById(R.id.premium_button_container)
        txtPremiumBtn = view.findViewById(R.id.txt_premium_btn)
        centerContainer = view.findViewById(R.id.center_container)
        rvPopulatedContent = view.findViewById(R.id.rv_populated_content)

        refreshUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }

    private fun refreshUI() {
        when (tabIndex) {
            0 -> {
                // Daftar Tonton
                if (MockData.watchlist.isNotEmpty()) {
                    centerContainer.visibility = View.GONE
                    actionButton.visibility = View.GONE
                    premiumBtnContainer.visibility = View.GONE
                    rvPopulatedContent.visibility = View.VISIBLE
                    rvPopulatedContent.layoutManager = LinearLayoutManager(context)
                    rvPopulatedContent.adapter = AnimePosterAdapter(MockData.watchlist) { anime ->
                        (activity as? MainActivity)?.openDetailFragment(anime)
                    }
                } else {
                    centerContainer.visibility = View.VISIBLE
                    actionButton.visibility = View.VISIBLE
                    premiumBtnContainer.visibility = View.GONE
                    rvPopulatedContent.visibility = View.GONE
                    illustration.setImageResource(R.drawable.cat_tv)
                    titleText.text = "Daftar tontonmu butuh perhatian."
                    subtitleText.text = "Ayo isi dengan anime keren."
                    actionButton.text = "Jelajahi Semua"
                    actionButton.setOnClickListener {
                        (activity as? MainActivity)?.selectBottomTab(R.id.navigation_telusuri)
                    }
                }
            }
            1 -> {
                // Crunchylist
                if (MockData.createdCrunchylists.isNotEmpty()) {
                    centerContainer.visibility = View.GONE
                    actionButton.visibility = View.GONE
                    premiumBtnContainer.visibility = View.GONE
                    rvPopulatedContent.visibility = View.VISIBLE
                    rvPopulatedContent.layoutManager = LinearLayoutManager(context)
                    rvPopulatedContent.adapter = CrunchylistAdapter(MockData.createdCrunchylists) { crunchylist ->
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_container, CrunchylistDetailFragment.newInstance(crunchylist.id))
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                } else {
                    centerContainer.visibility = View.VISIBLE
                    actionButton.visibility = View.VISIBLE
                    premiumBtnContainer.visibility = View.GONE
                    rvPopulatedContent.visibility = View.GONE
                    illustration.setImageResource(R.drawable.cat_bookshelf)
                    titleText.text = "Kamu belum memiliki Crunchylist."
                    subtitleText.text = "Ayo buat!"
                    actionButton.text = "Buat Daftar Baru"
                    actionButton.setOnClickListener {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_container, CreateCrunchylistFragment())
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }
            }
            2 -> {
                // Riwayat
                if (MockData.watchHistory.isNotEmpty()) {
                    centerContainer.visibility = View.GONE
                    actionButton.visibility = View.GONE
                    premiumBtnContainer.visibility = View.GONE
                    rvPopulatedContent.visibility = View.VISIBLE
                    rvPopulatedContent.layoutManager = LinearLayoutManager(context)
                    rvPopulatedContent.adapter = AnimePosterAdapter(MockData.watchHistory) { anime ->
                        (activity as? MainActivity)?.openDetailFragment(anime)
                    }
                } else {
                    centerContainer.visibility = View.VISIBLE
                    actionButton.visibility = View.VISIBLE
                    premiumBtnContainer.visibility = View.GONE
                    rvPopulatedContent.visibility = View.GONE
                    illustration.setImageResource(R.drawable.hime_laptop)
                    titleText.text = "Buat sejarah... dengan riwayat."
                    subtitleText.text = "Mulai menonton untuk mengisi feed ini."
                    actionButton.text = "Jelajahi Semua"
                    actionButton.setOnClickListener {
                        (activity as? MainActivity)?.selectBottomTab(R.id.navigation_telusuri)
                    }
                }
            }
            3 -> {
                // Unduhan
                if (MockData.activeSubscriptionPlan != "Gratis") {
                    if (MockData.downloads.isNotEmpty()) {
                        centerContainer.visibility = View.GONE
                        actionButton.visibility = View.GONE
                        premiumBtnContainer.visibility = View.GONE
                        rvPopulatedContent.visibility = View.VISIBLE
                        rvPopulatedContent.layoutManager = LinearLayoutManager(context)
                        rvPopulatedContent.adapter = AnimePosterAdapter(MockData.downloads) { anime ->
                            (activity as? MainActivity)?.openDetailFragment(anime)
                        }
                    } else {
                        centerContainer.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        premiumBtnContainer.visibility = View.VISIBLE
                        rvPopulatedContent.visibility = View.GONE
                        illustration.setImageResource(R.drawable.hime_laptop)
                        titleText.text = "Belum ada unduhan..."
                        subtitleText.text = "Akses Unduhan Offline aktif dengan plan: ${MockData.activeSubscriptionPlan}"
                        txtPremiumBtn.text = "Kelola Langganan"
                        premiumBtnContainer.setOnClickListener {
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.fragment_container, SubscriptionFragment())
                                ?.addToBackStack(null)
                                ?.commit()
                        }
                    }
                } else {
                    centerContainer.visibility = View.VISIBLE
                    actionButton.visibility = View.GONE
                    premiumBtnContainer.visibility = View.VISIBLE
                    rvPopulatedContent.visibility = View.GONE
                    illustration.setImageResource(R.drawable.hime_premium)
                    titleText.text = "Belum ada unduhan..."
                    subtitleText.text = "Tingkatkan ke Mega Fan or Mega Fan untuk fitur ini."
                    txtPremiumBtn.text = "Jadilah Premium"
                    premiumBtnContainer.setOnClickListener {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_container, SubscriptionFragment())
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }
            }
        }
    }

    // Nested Adapter for Crunchylists
    class CrunchylistAdapter(
        private val lists: List<CrunchylistModel>,
        private val onItemClick: (CrunchylistModel) -> Unit
    ) : RecyclerView.Adapter<CrunchylistAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_crunchylist, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = lists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = lists[position]
            holder.txtListName.text = item.name
            holder.txtListCount.text = "${item.animeIds.size} item"
            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txtListName: TextView = view.findViewById(R.id.txt_list_name)
            val txtListCount: TextView = view.findViewById(R.id.txt_list_count)
        }
    }
}
