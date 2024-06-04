package com.qnecesitas.retentienda.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.qnecesitas.retentienda.R
import com.qnecesitas.retentienda.data.Product
import com.qnecesitas.retentienda.databinding.RecyclerProductBinding

class AdapterProduct(private val context: Context) :
    ListAdapter<Product , AdapterProduct.ProductViewHolder>(DiffCallback) {

    private var clickEdit: ITouchEdit? = null
    private var clickDelete: ITouchDelete? = null
    private var clickSales: ITouchSales? = null
    private var clickDetails: ITouchDetails? = null
    private var clickEntry: ITouchEntry? = null


    class ProductViewHolder(private var binding: RecyclerProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            product: Product ,
            context: Context ,
            clickEdit: ITouchEdit? ,
            clickDelete: ITouchDelete? ,
            clickSales: ITouchSales? ,
            clickDetails: ITouchDetails? ,
            clickEntry: ITouchEntry?
        ) {

            //Declare
            val name = product.name
            val image = product.image
            val amount= product.amount
            val brand = product.brand
            val salePrice = product.salePrice




            binding.tvName.text = name
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
            binding.tvAmount.text = amount.toString()
            binding.tvBrand.text = brand
            binding.tvSalePrice.text = salePrice.toString()
            binding.menuOption.setOnClickListener {
                val popupMenu = PopupMenu(context , binding.menuOption)
                popupMenu.menuInflater.inflate(R.menu.menu_product , popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            clickEdit?.onClickEdit(product)

                        }

                        R.id.menu_delete -> {
                            clickDelete?.onClickDelete(product)
                        }

                        R.id.menu_sales -> {
                            clickSales?.onClickSales(product)
                        }

                        R.id.menu_details -> {
                            clickDetails?.onClickDetails(product)
                        }

                        R.id.menu_entry -> {
                            clickEntry?.onClickEntry(product)
                        }
                    }
                    false
                }
                popupMenu.show()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ProductViewHolder {
        val viewHolder = ProductViewHolder(
            RecyclerProductBinding.inflate(
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


    override fun onBindViewHolder(holder: ProductViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context ,
            clickEdit ,
            clickDelete ,
            clickSales ,
            clickDetails ,
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

    //Details
    interface ITouchEdit {
        fun onClickEdit(product: Product)
    }

    fun setClickEdit(clickEdit: ITouchEdit?) {
        this.clickEdit = clickEdit
    }

    interface ITouchDelete {
        fun onClickDelete(product: Product)
    }

    fun setClickDelete(clickDelete: ITouchDelete?) {
        this.clickDelete = clickDelete
    }

    interface ITouchSales {
        fun onClickSales(product: Product)
    }

    fun setClickSales(clickSales: ITouchSales?) {
        this.clickSales = clickSales
    }

    interface ITouchDetails {
        fun onClickDetails(product: Product)
    }

    fun setClickDetails(clickDetails: ITouchDetails?) {
        this.clickDetails = clickDetails
    }

    interface ITouchEntry {
        fun onClickEntry(product: Product)
    }

    fun setClickEntry(clickEntry: ITouchEntry?) {
        this.clickEntry = clickEntry
    }



}