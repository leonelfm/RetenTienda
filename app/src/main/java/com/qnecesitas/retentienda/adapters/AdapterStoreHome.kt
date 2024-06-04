package com.qnecesitas.retentienda.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qnecesitas.retentienda.R
import com.qnecesitas.retentienda.data.StoreHome
import com.qnecesitas.retentienda.databinding.RecyclerStoreHomeBinding

class AdapterStoreHome(private val context: Context) :
    ListAdapter<StoreHome , AdapterStoreHome.StoreHomeViewHolder>(DiffCallback)  {

    private var clickSelector: ITouchSelector? = null


    class StoreHomeViewHolder(private var binding: RecyclerStoreHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            store: StoreHome ,
            context: Context ,
            clickSelector: ITouchSelector?
        ) {

            //Declare
            val name = store.name




            binding.tvStore.text = name
            binding.root.setOnClickListener {
                clickSelector?.onClickSelector(store,adapterPosition)

            }
            when(store.state){
                "Seleccionado"->{
                    binding.tvStore.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_primary))
                    binding.view.setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_primary))
                }
                "No Seleccionado"->{
                    binding.tvStore.setTextColor(ContextCompat.getColor(context, R.color.black))
                    binding.view.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): StoreHomeViewHolder {
        val viewHolder = StoreHomeViewHolder(
            RecyclerStoreHomeBinding.inflate(
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


    override fun onBindViewHolder(holder: StoreHomeViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context ,
            clickSelector
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StoreHome>() {
            override fun areItemsTheSame(oldItem: StoreHome , newItem: StoreHome): Boolean {
                return oldItem == newItem && oldItem.state == newItem.state
            }

            override fun areContentsTheSame(oldItem: StoreHome , newItem: StoreHome): Boolean {
                return oldItem == newItem && oldItem.state == newItem.state
            }

        }
    }

    //Details
    interface ITouchSelector {
        fun onClickSelector(store: StoreHome,position: Int)
    }

    fun setClickSelector(clickSelector: ITouchSelector?) {
        this.clickSelector = clickSelector
    }

}