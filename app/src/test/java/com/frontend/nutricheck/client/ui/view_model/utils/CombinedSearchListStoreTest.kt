package com.frontend.nutricheck.client.ui.view_model.utils

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CombinedSearchListStoreTest {

    private lateinit var store: CombinedSearchListStore

    private val foodProduct = FoodProduct(
        id = "fp1",
        name = "Pasta",
        calories = 200.0,
        carbohydrates = 30.0,
        protein = 10.0,
        fat = 5.0,
        servings = 1.0,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    @Before
    fun setUp() { store = CombinedSearchListStore()
    }

    @Test
    fun `state emits initial empty then updated list`() = runTest {
        assertTrue(store.state.value.isEmpty())

        store.update(listOf(foodProduct))

        assertEquals(listOf(foodProduct), store.state.value)
    }
}