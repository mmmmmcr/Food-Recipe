package com.app.food.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.food.R
import com.app.food.databinding.IngredientsRowLayoutBinding
import com.app.food.models.ExtendedIngredient
import com.app.food.util.Constants.Companion.BASE_IMAGE_URL
import com.app.food.util.RecipesDiffUtil
import java.util.*

class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    private var ingredientsList = emptyList<ExtendedIngredient>()

    class ViewHolder(val binding: IngredientsRowLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(IngredientsRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ingredientImageView.load(BASE_IMAGE_URL + ingredientsList[position].image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        holder.binding.ingredientName.text = ingredientsList[position].name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
        holder.binding.ingredientAmount.text = ingredientsList[position].amount.toString()
        holder.binding.ingredientUnit.text = ingredientsList[position].unit
        holder.binding.ingredientConsistency.text = ingredientsList[position].consistency
        holder.binding.ingredientOriginal.text = ingredientsList[position].original
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun setData(newIngredients: List<ExtendedIngredient>) {
        val ingredientsDiffUtil =
            RecipesDiffUtil(ingredientsList, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }

}