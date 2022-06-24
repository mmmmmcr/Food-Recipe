package com.app.food.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.food.adapters.RecipesAdapter
import com.app.food.databinding.FragmentRecipesRandomBinding
import com.app.food.models.Result
import com.app.food.util.Constants.Companion.DEFAULT_RANDOM_RECIPES_NUMBER
import com.app.food.util.NetworkListener
import com.app.food.util.NetworkResult
import com.app.food.util.observeOnce
import com.app.food.viewmodels.MainViewModel
import com.app.food.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RandomFoodFragment : Fragment(){
    private val args by navArgs<RandomFoodFragmentArgs>()
    private val adapter by lazy { RecipesAdapter(true) }

    private var initialBinding: FragmentRecipesRandomBinding? = null

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    private lateinit var networkListener: NetworkListener

    override fun onResume() {
        super.onResume()
        if(mainViewModel.recyclerViewState != null){
            initialBinding!!.recyclerview.layoutManager?.onRestoreInstanceState(mainViewModel.recyclerViewState)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initialBinding = FragmentRecipesRandomBinding.inflate(inflater, container, false)
        initialBinding!!.lifecycleOwner = this
        initialBinding!!.mainViewModel = mainViewModel

        setHasOptionsMenu(false)

        setupRecyclerView()

        initialBinding!!.randomButton.setOnClickListener { readDatabase() }

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }
        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        initialBinding!!.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                val action =
                    RandomFoodFragmentDirections.actionRandomReceipesFragmentToRecipesBottomSheet(true)
                findNavController().navigate(action)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        return initialBinding!!.root
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    setData(database.first().foodRecipe.results)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries(DEFAULT_RANDOM_RECIPES_NUMBER))
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { setData(it.results) }
                    recipesViewModel.saveMealAndDietType()
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }


    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    setData(database.first().foodRecipe.results)
                }
            }
        }
    }

    private fun setData(database: List<Result>) {
        val randomIndex = Random().nextInt(database.size - 1)
        val foodRecipe = database[randomIndex]
        adapter.setData(foodRecipe)
    }

    private fun showShimmerEffect() {
        initialBinding!!.shimmerFrameLayout.startShimmer()
        initialBinding!!.shimmerFrameLayout.visibility = View.VISIBLE
        initialBinding!!.recyclerview.visibility = View.GONE
        initialBinding!!.randomButton.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        initialBinding!!.shimmerFrameLayout.stopShimmer()
        initialBinding!!.shimmerFrameLayout.visibility = View.GONE
        initialBinding!!.recyclerview.visibility = View.VISIBLE
        initialBinding!!.randomButton.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.recyclerViewState =
            initialBinding!!.recyclerview.layoutManager?.onSaveInstanceState()
        initialBinding = null
    }

    private fun setupRecyclerView() {
        initialBinding!!.recyclerview.adapter = adapter
        initialBinding!!.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }
}