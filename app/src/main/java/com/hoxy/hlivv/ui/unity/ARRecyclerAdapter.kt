package com.hoxy.hlivv.ui.unity

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoxy.hlivv.databinding.ItemArRecyclerBinding

class ARRecyclerAdapter(
    val context: Context,
    val list: List<ARCartItem>,
    val onClick: (item: ARCartItem) -> Unit
) :
    RecyclerView.Adapter<ARRecyclerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemArRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemArRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(list[position]) {
            holder.binding.itemContainer.setOnClickListener {
                onClick(this)
            }
            holder.binding.tvArItemTitle.text = this.productName
            Glide.with(context)
                .load(this.productImageUrl)
                .into(
                    holder.binding.ivArItemImage
                )
        }
    }
}