package com.saehyun.a09_android.remote

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saehyun.a09_android.R
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.ui.activity.PostActivity
import com.saehyun.a09_android.viewModel.PostLikeViewModel
import com.saehyun.a09_android.viewModel.PostViewModel
import com.saehyun.a09_android.viewModelFactory.PostViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.Response

class RcProductRvAdapter(val context: Context, val productData: List<PostValue>, val postLikeViewModel: PostLikeViewModel):
    RecyclerView.Adapter<RcProductRvAdapter.Holder>() {


    private val TAG = "RcProductRvAdapter"

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val clRecommendedProduct =
            itemView?.findViewById<ConstraintLayout>(R.id.clRecommendedProduct)
        val ivImage = itemView?.findViewById<ImageView>(R.id.ivImage)
        val tvTitle = itemView?.findViewById<TextView>(R.id.tvTitle)
        val tvTransactionRegion = itemView?.findViewById<TextView>(R.id.tvTransactionRegion)
        val tvPrice = itemView?.findViewById<TextView>(R.id.tvPrice)
        val ivHeart = itemView?.findViewById<ImageView>(R.id.ivHeart)
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

        holder.ivHeart?.setOnClickListener {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_on)
            postLikeViewModel.authPostLikeSearch(data.id.toInt())
        }

        if(data.liked) {
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_heart_on)
                .into(holder.ivHeart)
        }

        holder.clRecommendedProduct?.setOnClickListener {
            var intent = Intent(holder.itemView?.context, PostActivity::class.java)
            intent.putExtra("postId", data.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return productData.size
    }
}