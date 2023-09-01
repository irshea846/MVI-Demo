package com.example.mvi_architecture.uis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvi_architecture.data.model.User
import com.example.mvi_architecture.databinding.UserRowBinding

class MainAdapter(
    private val users: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class DataViewHolder(
        private val binding: UserRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.textViewUserName.text = buildString {
                append(user.first_name)
                append(" ")
                append(user.last_name)
            }
            binding.textViewUserEmail.text = user.email
            Glide.with(binding.imageViewAvatar.context)
                .load(user.avatar)
                .into(binding.imageViewAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: UserRowBinding =
            UserRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: User = users[position]
        when (holder) {
            is DataViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = users.size

    fun addData(list: List<User>) {
        val size = users.size
        users.addAll(index = if (size == 0) 0 else size - 1, list)
        notifyItemInserted(size)
    }

}