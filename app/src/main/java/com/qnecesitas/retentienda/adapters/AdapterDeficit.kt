package com.qnecesitas.retentienda.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.qnecesitas.retentienda.R
import com.qnecesitas.retentienda.data.Product
import com.qnecesitas.retentienda.databinding.RecyclerDeficitBinding

class AdapterDeficit (private val context: Context):
    ListAdapter<Product , AdapterDeficit.DeficitViewHolder>(DiffCallback) {

    private var clickEntry: ITouchEntry? = null


    class DeficitViewHolder(private var binding: RecyclerDeficitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            product: Product ,
            context:Context,
            clickEntry: ITouchEntry?
        ) {

            //Declare
            val amount = product.amount
            val name = product.name
            val store = product.store
            val file = product.file
            val image=product.image



            binding.tvName.text = name
            binding.tvAmount.text = amount.toString()
            binding.tvStore.text = store
            binding.tvFile.text = file
            image.let {
                val decodedBytes = Base64.decode(it, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                Glide.with(binding.image.context)
                    .load(bitmap)
                    .error(R.drawable.baseline_image_search_24)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Desactiva la caché para cargar la imagen cada vez
                    .skipMemoryCache(true) // Desactiva la caché en memoria para cargar la imagen cada vez
                    .into(binding.image)
            }
            binding.tvEntry.setOnClickListener { clickEntry?.onClickEntry(product) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): DeficitViewHolder {
        val viewHolder = DeficitViewHolder(
            RecyclerDeficitBinding.inflate(
                LayoutInflater.from(parent.context) ,
                parent ,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: DeficitViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context,
            clickEntry
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product , newItem: Product): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Product , newItem: Product): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface ITouchEntry {
        fun onClickEntry(product: Product)
    }

    fun setClickEntry(clickEntry: ITouchEntry?) {
        this.clickEntry = clickEntry
    }


}