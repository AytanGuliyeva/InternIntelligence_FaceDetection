package com.example.internintelligence_facedetection.ui.faceDatabase.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.internintelligence_facedetection.R
import com.example.internintelligence_facedetection.data.Users
import com.example.internintelligence_facedetection.databinding.ItemUsersBinding
import javax.microedition.khronos.opengles.GL

class UsersAdapter :  RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(){

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Users>() {
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallBack)

    fun submitList(userList: List<Users>) {
        differ.submitList(userList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = ItemUsersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsersViewHolder(binding)    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
    inner class UsersViewHolder(private val binding: ItemUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(users: Users) {
            binding.username.text = users.username
            binding.boundingBox.text = users.boundingBox

            if (users.image.isNotEmpty()) {
                val bitmap = decodeBase64ToBitmap(users.image)

                Glide.with(binding.imageUser.context)
                    .load(bitmap)
                    .into(binding.imageUser)
            }
        }

        private fun decodeBase64ToBitmap(base64String: String): Bitmap {
            return try {
                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    ?: throw IllegalArgumentException("Bitmap cannot be decoded")
            } catch (e: Exception) {
                Log.e("Adapter", "Failed to convert image: ${e.message}")
                BitmapFactory.decodeResource(binding.imageUser.context.resources, R.drawable.ic_launcher_foreground)
            }
        }
    }

}