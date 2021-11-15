package com.saehyun.a09_android.remote

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saehyun.a09_android.R
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.ui.activity.PostActivity
import com.saehyun.a09_android.viewModel.like.PostDeleteLikeViewModel
import com.saehyun.a09_android.viewModel.like.PostLikeViewModel
import org.w3c.dom.Text

class RcProductRvAdapter(val context: Context, val productData: List<PostValue>, val postLikeViewModel: PostLikeViewModel, val postDeleteLikeViewModel: PostDeleteLikeViewModel):
    RecyclerView.Adapter<RcProductRvAdapter.Holder>() {
    
    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val clRecommendedProduct =
            itemView?.findViewById<ConstraintLayout>(R.id.clRecommendedProduct)
        val ivImage = itemView?.findViewById<ImageView>(R.id.ivImage)
        val tvTitle = itemView?.findViewById<TextView>(R.id.tvTitle)
        val tvTransactionRegion = itemView?.findViewById<TextView>(R.id.tvTransactionRegion)
        val tvPrice = itemView?.findViewById<TextView>(R.id.tvPrice)
        val ivHeart = itemView?.findViewById<ImageView>(R.id.ivHeart)
        val ivPurpose = itemView?.findViewById<ImageView>(R.id.ivPurpose)
        val rvisible1 = itemView?.findViewById<View>(R.id.rvisible1)
        val rvisible2 = itemView?.findViewById<TextView>(R.id.rvisible2)
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

        holder.ivHeart?.setOnClickListener {
            if(data.liked) {
                holder.ivHeart.setImageResource(R.drawable.ic_heart_off)
                postDeleteLikeViewModel.memberDeleteLike(data.id.toInt())
                data.liked = false
            } else {
                holder.ivHeart.setImageResource(R.drawable.ic_heart_on)
                postLikeViewModel.authPostLikeSearch(data.id.toInt())
                data.liked = true
            }
        }

        if(data.liked) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_heart_on)
                .into(holder.ivHeart)
        }

        if(data.price.isNullOrEmpty()) {
            holder.tvPrice?.text = "무료나눔"
            holder.ivPurpose?.visibility = View.GONE
        } else {
            holder.tvPrice?.text = data.price
        }

        holder.clRecommendedProduct?.setOnClickListener {
            var intent = Intent(holder.itemView?.context, PostActivity::class.java)
            intent.putExtra("postId", data.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        if(data.completed == "COMPLETED") {
            holder.rvisible1?.visibility = View.VISIBLE
            holder.rvisible2?.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return productData.size
    }
}