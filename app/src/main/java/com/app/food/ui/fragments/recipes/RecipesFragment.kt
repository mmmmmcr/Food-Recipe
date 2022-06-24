package com.app.food.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.food.viewmodels.MainViewModel
import com.app.food.R
import com.app.food.adapters.RecipesAdapter
import com.app.food.databinding.FragmentRecipesBinding
import com.app.food.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.app.food.util.NetworkListener
import com.app.food.util.NetworkResult
import com.app.food.util.observeOnce
import com.app.food.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val args by navArgs<RecipesFragmentArgs>()

    private var initialBinding: FragmentRecipesBinding? = null

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val adapter by lazy { RecipesAdapter(false) }

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
        initialBinding = FragmentRecipesBinding.inflate(inflater, container, false)
        initialBinding!!.lifecycleOwner = this
        initialBinding!!.mainViewModel = mainViewModel

        setHasOptionsMenu(true)

        setupRecyclerView()

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
                    RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet(false)
                findNavController().navigate(action)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        return initialBinding!!.root
    }

    private fun setupRecyclerView() {
        initialBinding!!.recyclerview.adapter = adapter
        initialBinding!!.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    adapter.setData(database.first().foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData(Integer.valueOf(DEFAULT_RECIPES_NUMBER))
                }
            }
        }
    }

    private fun requestApiData(no: Int) {
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.recyclerViewState =
            initialBinding!!.recyclerview.layoutManager?.onSaveInstanceState()
        mainViewModel.getRecipes(recipesViewModel.applyQueries(no.toString()))
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { adapter.setData(it) }
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

    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let { adapter.setData(it) }
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
                    adapter.setData(database.first().foodRecipe)
                }
            }
        }
    }

    private fun showShimmerEffect() {
        initialBinding!!.shimmerFrameLayout.startShimmer()
        initialBinding!!.shimmerFrameLayout.visibility = View.VISIBLE
        initialBinding!!.recyclerview.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        initialBinding!!.shimmerFrameLayout.stopShimmer()
        initialBinding!!.shimmerFrameLayout.visibility = View.GONE
        initialBinding!!.recyclerview.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.recyclerViewState =
            initialBinding!!.recyclerview.layoutManager?.onSaveInstanceState()
        initialBinding = null
    }
}