package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.history.BaseHistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe_list.BaseRecipeListViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RecipeListViewModel : BaseRecipeListViewModel() {

    sealed interface Event {
        object ClickAddRecipe : Event
        object ClickRecipe : Event
        object ClickDetails : Event
        object ClickMyRecipes : Event
        object ClickOnlineRecipes : Event
    }

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    override fun onClickAddRecipe() { emitEvent(Event.ClickAddRecipe) }
    override fun onRecipeClick() { emitEvent(Event.ClickRecipe) }
    override fun onDetailsClick() { emitEvent(Event.ClickDetails) }
    override fun onMyRecipesClick() { emitEvent(Event.ClickMyRecipes) }
    override fun onOnlieRecipesClick() { emitEvent(Event.ClickOnlineRecipes) }

    private fun emitEvent(event: Event) = viewModelScope.launch { _events.emit(event) }

    companion object

}