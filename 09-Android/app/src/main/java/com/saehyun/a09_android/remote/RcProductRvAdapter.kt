package com.saehyun.a09_android.remote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saehyun.a09_android.R
import com.saehyun.a09_android.model.data.PostValue

class RcProductRvAdapter(val context: Context, val productData: List<PostValue>):
    RecyclerView.Adapter<RcProductRvAdapter.Holder>() {


    private val TAG = "RcProductRvAdapter"

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val clRecommendedProduct =
            itemView?.findViewById<ConstraintLayout>(R.id.clRecommendedProduct)
        val ivImage = itemView?.findViewById<ImageView>(R.id.ivImage)
        val ivPurpose = itemView?.findViewById<ImageView>(R.id.ivPurpose)
        val tvTitle = itemView?.findViewById<TextView>(R.id.tvTitle)
        val tvTransactionRegion = itemView?.findViewById<TextView>(R.id.tvTransactionRegion)
        val tvPrice = itemView?.findViewById<TextView>(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recommended_product_view, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        var data: PostValue = productData.get(position)

        Glide.with(holder.itemView.context)
                .load(data.image)
                .into(holder.ivImage)

        holder.tvTitle?.text = data.title
        holder.tvTransactionRegion?.text = data.transaction_region
        holder.tvPrice?.text = data.price

    }

    override fun getItemCount(): Int {
        return productData.size
    }
}