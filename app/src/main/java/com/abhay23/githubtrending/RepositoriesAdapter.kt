package com.abhay23.githubtrending

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abhay23.githubtrending.model.Repository

class RepositoriesAdapter(private val repositories: MutableList<Repository> = mutableListOf()) :
    RecyclerView.Adapter<RepositoriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.trending_repository_item, parent, false)
        return RepositoriesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RepositoriesViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    override fun getItemCount(): Int {
        return repositories.size
    }

    fun updateData(repositories: List<Repository>) {
        val callback =
            RepositoriesDiffUtilCallback(newList = repositories, oldList = this.repositories)
        val diffResult = DiffUtil.calculateDiff(callback)
        this.repositories.clear()
        this.repositories.addAll(repositories)
        diffResult.dispatchUpdatesTo(this)
    }

    fun sortRepositoriesByStars() {
        val sortedList = repositories
            .sortedBy { repository: Repository -> repository.starsCount }
        updateData(sortedList)
    }

    fun sortRepositoriesByName() {
        val sortedList =
            repositories.sortedBy { repository: Repository -> repository.repositoryName }
        updateData(sortedList)
    }
}

class RepositoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val llRepositoryItemContainer =
        itemView.findViewById<LinearLayout>(R.id.ll_repository_item_container)
    private val llStatsContainer = itemView.findViewById<LinearLayout>(R.id.ll_stats_container)
    private val tvOrganizationName = itemView.findViewById<TextView>(R.id.tv_organization_name)
    private val tvRepositoryName = itemView.findViewById<TextView>(R.id.tv_repository_name)
    private val tvRepositoryDescription =
        itemView.findViewById<TextView>(R.id.tv_repository_description)
    private val tvLanguage = itemView.findViewById<TextView>(R.id.tv_language)
    private val tvStars = itemView.findViewById<TextView>(R.id.tv_stars)
    private val tvForks = itemView.findViewById<TextView>(R.id.tv_forks)

    fun bind(repository: Repository) {
        tvOrganizationName.text = repository.organizationName
        tvRepositoryName.text = repository.repositoryName
        tvRepositoryDescription.text = repository.repositoryDescription
        tvLanguage.text = repository.languageName
        tvStars.text = repository.starsCount.toString()
        tvForks.text = repository.forksCount.toString()
        llRepositoryItemContainer.setOnClickListener {
            if (llStatsContainer.visibility != View.VISIBLE) {
                llStatsContainer.visibility = View.VISIBLE
                tvRepositoryDescription.visibility = View.VISIBLE
            } else {
                llStatsContainer.visibility = View.GONE
                tvRepositoryDescription.visibility = View.GONE
            }
        }
    }
}