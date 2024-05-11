package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ItemListListener
import com.example.myapplication.data.Articles
import com.example.myapplication.databinding.ListItemBinding

class ArticleAdapter(
    private val itemListListener: ItemListListener,
    private val articles: ArrayList<Articles>
) :
    RecyclerView.Adapter<ArticleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(layoutInflater, parent, false)
        return ArticleViewHolder(binding)
    }

    fun updateArticles(newCountries: List<Articles>) {
        articles.clear()
        articles.addAll(newCountries)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position], itemListListener)
    }
}

class ArticleViewHolder(
    private val binding: ListItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(articles: Articles, itemListListener: ItemListListener) {
        binding.data = articles
        binding.mainLayout.setOnClickListener {
            articles.url.let {
                if (it != null) {
                    itemListListener.onClick(it)
                }
            }
        }
        binding.executePendingBindings()
    }
}