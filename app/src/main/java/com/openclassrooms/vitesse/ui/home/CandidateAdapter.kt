package com.openclassrooms.vitesse.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.vitesse.domain.model.Candidate

/**
 * Adapter for displaying a list of [Candidate] in a RecyclerView.
 * Uses ListAdapter and DiffUtil for efficient updates.
 *
 * @param itemClickListener Callback for item click events.
 */
class CandidateAdapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<Candidate, CandidateAdapter.CandidateViewHolder>(DIFF_CALLBACK) {

    /**
     * Listener interface for handling item click events.
     */
    interface OnItemClickListener {
        fun onItemClick(item: Candidate)
    }

    /**
     * ViewHolder class that binds a [Candidate] to its layout.
     */
    class CandidateViewHolder(
        private val binding: ItemCandidateBinding,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the given [Candidate] to the views.
         */
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
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateBinding.inflate(inflater, parent, false)
        return CandidateViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * DiffUtil callback to efficiently determine list changes.
         */
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Candidate>() {
            override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                // Use unique ID comparison if available instead of reference equality
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                return oldItem == newItem
            }
        }
    }
}
