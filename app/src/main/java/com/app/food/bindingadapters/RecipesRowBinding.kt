package com.app.food.bindingadapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.app.food.R
import com.app.food.models.Result
import com.app.food.ui.fragments.recipes.RandomFoodFragmentDirections
import com.app.food.ui.fragments.recipes.RecipesFragmentDirections
import org.jsoup.Jsoup
import java.lang.Exception

class RecipesRowBinding {

    companion object {

        @BindingAdapter(value = ["bind:result", "bind:fromRandomFrag"])
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: Result, isFromRandomFragment: Boolean) {
            recipeRowLayout.setOnClickListener {
                try {
                    if (isFromRandomFragment) {
                        val action =
                            RandomFoodFragmentDirections.actionRandomFoodFragmentToDetailsActivity(result)
                        recipeRowLayout.findNavController().navigate(action)
                    } else {
                        val action =
                            RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                        recipeRowLayout.findNavController().navigate(action)
                    }
                } catch (e: Exception) {
                    Log.d(RecipesRowBinding.toString(), e.toString())
                }
            }
        }

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if (vegan) {
                when (view) {
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?){
            if(description != null) {
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }

    }

}