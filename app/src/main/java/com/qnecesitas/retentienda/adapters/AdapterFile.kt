package com.qnecesitas.retentienda.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qnecesitas.retentienda.data.File
import com.qnecesitas.retentienda.databinding.RecyclerFileBinding

class AdapterFile(private val context: Context):
    ListAdapter<File , AdapterFile.FileViewHolder>(DiffCallback){


    private var clickEdit: ITouchEdit? = null
    private var clickDelete: ITouchDelete? = null
    private var clickSelector:ITouchSelector? = null

    class FileViewHolder(private var binding: RecyclerFileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            file: File ,
            context: Context ,
            clickEdit: ITouchEdit? ,
            clickDelete: ITouchDelete? ,
            clickSelector: ITouchSelector? ,
        ) {

            //Declare
            val name = file.name



            binding.tvName.text = name
            binding.edit.setOnClickListener {clickEdit?.onClickEdit(file)}
            binding.delete.setOnClickListener { clickDelete?.onClickDelete(file) }
            binding.clContainer.setOnClickListener { clickSelector?.onClickSelector(file) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): FileViewHolder {
        val viewHolder = FileViewHolder(
            RecyclerFileBinding.inflate(
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


    override fun onBindViewHolder(holder: FileViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context ,
            clickEdit ,
            clickDelete ,
            clickSelector
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<File>() {
            override fun areItemsTheSame(oldItem: File , newItem: File): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: File , newItem: File): Boolean {
                return oldItem == newItem
            }

        }
    }

    //Details
    interface ITouchEdit {
        fun onClickEdit(file: File)
    }

    fun setClickEdit(clickEdit: ITouchEdit?) {
        this.clickEdit = clickEdit
    }

    interface ITouchDelete {
        fun onClickDelete(file: File)
    }

    fun setClickDelete(clickDelete: ITouchDelete?) {
        this.clickDelete = clickDelete
    }

    interface ITouchSelector {
        fun onClickSelector(file: File)
    }

    fun setClickSelector(clickSelector: ITouchSelector?) {
        this.clickSelector = clickSelector
    }



}