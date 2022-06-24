package com.app.food.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.food.databinding.RecipesRowLayoutBinding
import com.app.food.models.FoodRecipe
import com.app.food.models.Result
import com.app.food.util.RecipesDiffUtil
import java.util.ArrayList

class RecipesAdapter(private val isFromRandomFrag: Boolean) : RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {

    private var recipes = emptyList<Result>()

    class ViewHolder(private val binding: RecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Result, isFromRandomFrag: Boolean){
            binding.result = result
            binding.isFromRandomFragment = isFromRandomFrag
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe, isFromRandomFrag)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setData(newData: FoodRecipe){
        val newList = recipes + newData.results
        val recipesDiffUtil =
            RecipesDiffUtil(recipes, newList)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newList
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun setData(newData: Result){
        val list = ArrayList<Result>().apply {
            this.add(newData)
        }
        val recipesDiffUtil =
            RecipesDiffUtil(recipes, list)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = list
        diffUtilResult.dispatchUpdatesTo(this)
    }
}