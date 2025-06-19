package com.openclassrooms.vitesse.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.CandidateAdapter.CandidateViewHolder

class CandidateAdapter(private val itemClickListener: OnItemClickListener) :
    ListAdapter<Candidate, CandidateViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickListener {
        fun onItemClick(item: Candidate)
    }

    class CandidateViewHolder(
        private val binding: ItemCandidateBinding,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(candidate: Candidate) {
            binding.firstname.text = candidate.firstname
            binding.lastname.text = candidate.lastname
            binding.notes.text = candidate.notes
            Glide.with(binding.root.context)
                .load(candidate.profilePicture)
                .placeholder(R.drawable.ic_profile_pic)
                .error(R.drawable.ic_profile_pic)
                .into(binding.profilePicture)

                binding.root.setOnClickListener {
                itemClickListener.onItemClick(candidate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val itemView = ItemCandidateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CandidateViewHolder(itemView, itemClickListener)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Candidate> =
            object : DiffUtil.ItemCallback<Candidate>() {
                override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                    return oldItem === newItem
                }

                override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                    return oldItem == newItem
                }
            }
    }
}