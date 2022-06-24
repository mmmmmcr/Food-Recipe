package com.app.food.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.food.adapters.IngredientsAdapter
import com.app.food.databinding.FragmentIngredientsBinding
import com.app.food.models.Result
import com.app.food.util.Constants.Companion.RECIPE_RESULT_KEY

class IngredientsFragment : Fragment() {

    private val adapter: IngredientsAdapter by lazy { IngredientsAdapter() }

    private var initialBinding: FragmentIngredientsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initialBinding = FragmentIngredientsBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        setupRecyclerView()
        myBundle?.extendedIngredients?.let { adapter.setData(it) }

        return initialBinding!!.root
    }

    private fun setupRecyclerView() {
        initialBinding!!.ingredientsRecyclerview.adapter = adapter
        initialBinding!!.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initialBinding = null
    }
}