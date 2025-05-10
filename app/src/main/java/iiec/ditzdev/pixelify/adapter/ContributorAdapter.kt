package iiec.ditzdev.pixelify.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import iiec.ditzdev.pixelify.R
import iiec.ditzdev.pixelify.models.Contributor

class ContributorAdapter(private val contributors: List<Contributor>) :
    RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contributor, parent, false)
        return ContributorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        holder.bind(contributors[position])
    }

    override fun getItemCount(): Int = contributors.size

    inner class ContributorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView: ShapeableImageView = itemView.findViewById(R.id.iv_contributor_avatar)
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_contributor_name)
        private val roleTextView: TextView = itemView.findViewById(R.id.tv_contributor_role)
        private val githubButton: ImageButton = itemView.findViewById(R.id.btn_contributor_github)

        fun bind(contributor: Contributor) {
            nameTextView.text = contributor.name
            roleTextView.text = contributor.role

            // Load avatar image if available
            contributor.avatarUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder)
                    .into(avatarImageView)
            } ?: run {
                // Set default avatar if URL is null
                avatarImageView.setImageResource(R.drawable.ic_person_placeholder)
            }

            // Setup GitHub profile link button
            if (contributor.githubUrl != null) {
                githubButton.visibility = View.VISIBLE
                githubButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contributor.githubUrl))
                    itemView.context.startActivity(intent)
                }
            } else {
                githubButton.visibility = View.GONE
            }
        }
    }
}