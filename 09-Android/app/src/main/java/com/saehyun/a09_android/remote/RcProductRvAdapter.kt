package com.saehyun.a09_android.remote

import android.app.ActionBar
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatDrawableManager.preload
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.saehyun.a09_android.R
import com.saehyun.a09_android.model.data.RcProductRvData
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.task.URLtoBitmapTask
import java.net.URL

class RcProductRvAdapter(val context: Context, val productData: List<PostResponse>):
    RecyclerView.Adapter<RcProductRvAdapter.Holder>() {


    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val clRecommendedProduct =
            itemView?.findViewById<ConstraintLayout>(R.id.clRecommendedProduct)
        val ivImage = itemView?.findViewById<ImageView>(R.id.ivImage)
        val ivPurpose = itemView?.findViewById<ImageView>(R.id.ivPurpose)
        val tvTitle = itemView?.findViewById<TextView>(R.id.tvTitle)
        val tvTransactionRegion = itemView?.findViewById<TextView>(R.id.tvTransactionRegion)
        val tvPrice = itemView?.findViewById<TextView>(R.id.tvPrice)


        fun bind(postResponse: PostResponse, context: Context) {
            tvTitle?.text = postResponse.title
            tvTransactionRegion?.text = postResponse.transaction_region
            tvPrice?.text = postResponse.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recommended_product_view, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(productData[position], context)
    }

    override fun getItemCount(): Int {
        return productData.size
    }
}