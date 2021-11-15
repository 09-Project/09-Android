package com.saehyun.a09_android.remote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saehyun.a09_android.R
import com.saehyun.a09_android.model.response.MemberShowResponse

class MemberShowRvAdapter(val context: Context, val productData: List<MemberShowResponse>):
    RecyclerView.Adapter<MemberShowRvAdapter.Holder>() {

    private val TAG = "RcProductRvAdapter"

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val ivImage = itemView?.findViewById<ImageView>(R.id.ivImage)
        val tvTitle = itemView?.findViewById<TextView>(R.id.tvTitle)
        val ivHeart = itemView?.findViewById<TextView>(R.id.ivHeart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recommended_other_product_view, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        var data: MemberShowResponse = productData.get(position)
//
//        Glide.with(holder.itemView.context)
//                .
//load(data.image)
//                .into(holder.ivImage)
//        holder.tvTitle?.text = data.title
    }

    override fun getItemCount(): Int {
        return productData.size
    }
}