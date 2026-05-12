package com.dresta0056.whowantcoffee.ui.coffeelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dresta0056.whowantcoffee.WhoWantCoffeeApplication
import com.dresta0056.whowantcoffee.data.CoffeeRepository
import kotlinx.coroutines.launch

class CoffeeListViewModel(
    private val repository: CoffeeRepository
) : ViewModel() {

    val coffees = repository.activeCoffees()
    val sortOrder = repository.sortOrder
    val viewMode = repository.viewMode
    val archivedCount = repository.countArchived()

    fun toggleSort(current: String) {
        viewModelScope.launch {
            repository.toggleSort(current)
        }
    }

    fun toggleView(current: String) {
        viewModelScope.launch {
            repository.toggleView(current)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WhoWantCoffeeApplication
                CoffeeListViewModel(app.repository)
            }
        }
    }
}