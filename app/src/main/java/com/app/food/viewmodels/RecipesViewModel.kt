package com.app.food.viewmodels

import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.food.R
import com.app.food.data.DataStoreRepository
import com.app.food.data.MealAndDietType
import com.app.food.util.Constants.Companion.API_KEY
import com.app.food.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.app.food.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.app.food.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.app.food.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.app.food.util.Constants.Companion.QUERY_API_KEY
import com.app.food.util.Constants.Companion.QUERY_DIET
import com.app.food.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.app.food.util.Constants.Companion.QUERY_NUMBER
import com.app.food.util.Constants.Companion.QUERY_SEARCH
import com.app.food.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: android.app.Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private lateinit var mealAndDiet: MealAndDietType

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun saveMealAndDietType() =
        viewModelScope.launch(dispatcher) {
            if (this@RecipesViewModel::mealAndDiet.isInitialized) {
                dataStoreRepository.saveMealAndDietType(
                    mealAndDiet.selectedMealType,
                    mealAndDiet.selectedMealTypeId,
                    mealAndDiet.selectedDietType,
                    mealAndDiet.selectedDietTypeId
                )
            }
        }

    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDiet = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(dispatcher) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applyQueries(num : String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = num
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        if (this@RecipesViewModel::mealAndDiet.isInitialized) {
            queries[QUERY_TYPE] = mealAndDiet.selectedMealType
            queries[QUERY_DIET] = mealAndDiet.selectedDietType
        } else {
            queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
            queries[QUERY_DIET] = DEFAULT_DIET_TYPE
        }

        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), getApplication<com.app.food.Application>().getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus && backOnline) {
            Toast.makeText(getApplication(), getApplication<com.app.food.Application>().getString(R.string.back_online), Toast.LENGTH_SHORT).show()
            saveBackOnline(false)
        }
    }

}