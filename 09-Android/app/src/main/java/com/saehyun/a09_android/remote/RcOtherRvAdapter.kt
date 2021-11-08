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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saehyun.a09_android.R
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.model.response.PostOtherResponse
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.ui.activity.PostActivity

class RcOtherRvAdapter(val context: Context, val productData: List<PostOtherResponse>):
    RecyclerView.Adapter<RcOtherRvAdapter.Holder>() {

    private val TAG = "RcProductRvAdapter"

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val ivImage = itemView?.findViewById<ImageView>(R.id.ivImage)
        val tvTitle = itemView?.findViewById<TextView>(R.id.tvTitle)
        val ivHeart = itemView?.findViewById<ImageView>(R.id.ivOHeart)
        val clOtherProductView = itemView?.findViewById<ConstraintLayout>(R.id.clOtherProductView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recommended_other_product_view, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        var data: PostOtherResponse = productData.get(position)

        Glide.with(holder.itemView.context)
                .load(data.image)
                .into(holder.ivImage)

        holder.tvTitle?.text = data.title

        holder.clOtherProductView?.setOnClickListener {
            var intent = Intent(holder.itemView.context, PostActivity::class.java)
            intent.putExtra("postId", data.id.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

//        if(data.liked) {
//            Glide.with(holder.itemView.context)
//                .load(R.drawable.ic_heart_on)
//                .into(holder.ivHeart)
//        }
    }

    override fun getItemCount(): Int {
        return productData.size
    }
}