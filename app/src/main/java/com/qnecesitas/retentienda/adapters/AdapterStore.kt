package com.qnecesitas.retentienda.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qnecesitas.retentienda.data.Store
import com.qnecesitas.retentienda.databinding.RecyclerStoreBinding

class AdapterStore(private val context:Context):
    ListAdapter<Store , AdapterStore.StoreViewHolder>(DiffCallback){


    private var clickEdit: ITouchEdit? = null
    private var clickDelete: ITouchDelete? = null

    class StoreViewHolder(private var binding: RecyclerStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            store: Store ,
            context: Context ,
            clickEdit: ITouchEdit? ,
            clickDelete: ITouchDelete? ,
        ) {

            //Declare
            val name = store.name



            binding.tvName.text = name
            binding.edit.setOnClickListener {clickEdit?.onClickEdit(store)}
            binding.delete.setOnClickListener { clickDelete?.onClickDelete(store) }
    }
        }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): StoreViewHolder {
        val viewHolder = StoreViewHolder(
            RecyclerStoreBinding.inflate(
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


    override fun onBindViewHolder(holder: StoreViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context ,
            clickEdit ,
            clickDelete ,
            )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Store>() {
            override fun areItemsTheSame(oldItem: Store , newItem: Store): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Store , newItem: Store): Boolean {
                return oldItem == newItem
            }

        }
    }

    //Details
    interface ITouchEdit {
        fun onClickEdit(store: Store)
    }

    fun setClickEdit(clickEdit: ITouchEdit?) {
        this.clickEdit = clickEdit
    }

    interface ITouchDelete {
        fun onClickDelete(store: Store)
    }

    fun setClickDelete(clickDelete: ITouchDelete?) {
        this.clickDelete = clickDelete
    }



}