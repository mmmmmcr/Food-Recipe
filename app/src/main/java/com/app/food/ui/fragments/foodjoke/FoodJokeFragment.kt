package com.app.food.ui.fragments.foodjoke

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.food.R
import com.app.food.databinding.FragmentFoodJokeBinding
import com.app.food.util.Constants.Companion.API_KEY
import com.app.food.util.NetworkResult
import com.app.food.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()

    private var initialBinding: FragmentFoodJokeBinding? = null

    private var foodJoke: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        foodJoke = getString(R.string.no_food_joke)
        initialBinding = FragmentFoodJokeBinding.inflate(inflater, container, false)
        initialBinding!!.lifecycleOwner = viewLifecycleOwner
        initialBinding!!.mainViewModel = mainViewModel

        setHasOptionsMenu(true)

        mainViewModel.getFoodJoke(API_KEY)
        mainViewModel.foodJokeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    initialBinding!!.foodJokeTextView.text = response.data?.text
                    if (response.data != null) {
                        foodJoke = response.data.text
                    }
                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    Log.d("FoodJokeFragment", "Loading")
                }
            }
        }

        return initialBinding!!.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_joke_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_food_joke_menu) {
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, foodJoke)
                this.type = "text/plain"
            }
            startActivity(shareIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readFoodJoke.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    initialBinding!!.foodJokeTextView.text = database.first().foodJoke.text
                    foodJoke = database.first().foodJoke.text
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initialBinding = null
    }
}