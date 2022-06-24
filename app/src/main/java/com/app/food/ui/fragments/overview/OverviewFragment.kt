package com.app.food.ui.fragments.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.app.food.R
import com.app.food.bindingadapters.RecipesRowBinding
import com.app.food.databinding.FragmentOverviewBinding
import com.app.food.models.Result
import com.app.food.util.Constants.Companion.RECIPE_RESULT_KEY

class OverviewFragment : Fragment() {

    private var initialBinding: FragmentOverviewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initialBinding = FragmentOverviewBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result = args!!.getParcelable<Result>(RECIPE_RESULT_KEY) as Result

        initialBinding!!.mainImageView.load(myBundle.image)
        initialBinding!!.titleTextView.text = myBundle.title
        initialBinding!!.likesTextView.text = myBundle.aggregateLikes.toString()
        initialBinding!!.timeTextView.text = myBundle.readyInMinutes.toString()
        RecipesRowBinding.parseHtml(initialBinding!!.summaryTextView, myBundle.summary)

        updateColors(myBundle.vegetarian, initialBinding!!.vegetarianTextView, initialBinding!!.vegetarianImageView)
        updateColors(myBundle.vegan, initialBinding!!.veganTextView, initialBinding!!.veganImageView)
        updateColors(myBundle.cheap, initialBinding!!.cheapTextView, initialBinding!!.cheapImageView)
        updateColors(myBundle.dairyFree, initialBinding!!.dairyFreeTextView, initialBinding!!.dairyFreeImageView)
        updateColors(myBundle.glutenFree, initialBinding!!.glutenFreeTextView, initialBinding!!.glutenFreeImageView)
        updateColors(myBundle.veryHealthy, initialBinding!!.healthyTextView, initialBinding!!.healthyImageView)

        return initialBinding!!.root
    }

    private fun updateColors(stateIsOn: Boolean, textView: TextView, imageView: ImageView) {
        if (stateIsOn) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initialBinding = null
    }
}