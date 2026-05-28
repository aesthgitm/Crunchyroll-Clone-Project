package com.example.crunchyroll_pemvis_5

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class SubscriptionPlanAdapter(private var plans: List<SubscriptionPlan>) :
    RecyclerView.Adapter<SubscriptionPlanAdapter.ViewHolder>() {

    fun updatePlans(newPlans: List<SubscriptionPlan>) {
        plans = newPlans
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription_plan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = plans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plan = plans[position]
        holder.bind(plan)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgMascot: ImageView = view.findViewById(R.id.img_mascot)
        private val txtPlanTitle: TextView = view.findViewById(R.id.txt_plan_title)
        private val txtOldPrice: TextView = view.findViewById(R.id.txt_old_price)
        private val txtNewPrice: TextView = view.findViewById(R.id.txt_new_price)
        private val txtPromoTag: TextView = view.findViewById(R.id.txt_promo_tag)
        private val lblFeat4: TextView = view.findViewById(R.id.lbl_feat4)
        private val statusFeat6: TextView = view.findViewById(R.id.status_feat6)
        private val lblFeat6: TextView = view.findViewById(R.id.lbl_feat6)

        fun bind(plan: SubscriptionPlan) {
            imgMascot.setImageResource(plan.mascotResId)
            txtPlanTitle.text = plan.title
            txtNewPrice.text = plan.newPrice

            if (plan.oldPrice != null) {
                txtOldPrice.text = plan.oldPrice
                // Apply strike-through
                txtOldPrice.paintFlags = txtOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                txtOldPrice.visibility = View.VISIBLE
            } else {
                txtOldPrice.visibility = View.GONE
            }

            val isCurrentPlan = plan.title.equals(MockData.activeSubscriptionPlan, ignoreCase = true) ||
                    (plan.title.equals("PENGGEMAR", ignoreCase = true) && MockData.activeSubscriptionPlan.equals("FAN", ignoreCase = true)) ||
                    (plan.title.equals("FAN", ignoreCase = true) && MockData.activeSubscriptionPlan.equals("PENGGEMAR", ignoreCase = true))

            if (isCurrentPlan) {
                txtPromoTag.text = "PAKET AKTIF"
                txtPromoTag.visibility = View.VISIBLE
                txtPromoTag.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                txtPromoTag.setTextColor(android.graphics.Color.WHITE)
            } else if (plan.promoTag != null) {
                txtPromoTag.text = plan.promoTag
                txtPromoTag.visibility = View.VISIBLE
                txtPromoTag.setBackgroundColor(android.graphics.Color.parseColor("#FAB818"))
                txtPromoTag.setTextColor(android.graphics.Color.BLACK)
            } else {
                txtPromoTag.visibility = View.GONE
            }

            lblFeat4.text = "Streaming di ${plan.streamingPerangkat} perangkat"

            if (plan.hasGameVault) {
                statusFeat6.text = "✓"
                statusFeat6.setTextColor(ContextCompat.getColor(itemView.context, R.color.crunchyroll_gold))
                lblFeat6.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                statusFeat6.text = "✕"
                statusFeat6.setTextColor(ContextCompat.getColor(itemView.context, R.color.tab_unselected_grey))
                lblFeat6.setTextColor(ContextCompat.getColor(itemView.context, R.color.tab_unselected_grey))
            }
        }
    }
}
