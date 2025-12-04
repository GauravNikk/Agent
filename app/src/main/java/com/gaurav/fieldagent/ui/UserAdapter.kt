package com.gaurav.fieldagent.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gaurav.fieldagent.R
import com.gaurav.fieldagent.data.model.User

class UserAdapter(private val onItemClick: (User) -> Unit) : PagingDataAdapter<User, UserAdapter.UserViewHolder>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.user_name)
        private val userEmail: TextView = itemView.findViewById(R.id.user_email)
        private val userImage: ImageView = itemView.findViewById(R.id.user_image)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = getItem(position)
                    if (user != null) {
                        onItemClick(user)
                    }
                }
            }
        }

        fun bind(user: User) {
            userName.text = "${user.firstName} ${user.lastName}"
            userEmail.text = user.email
            // In a real app, you'd use a library like Glide or Coil to load the image
            // For now, we'll just set a placeholder
            userImage.setImageResource(R.mipmap.ic_launcher)
        }
    }

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}