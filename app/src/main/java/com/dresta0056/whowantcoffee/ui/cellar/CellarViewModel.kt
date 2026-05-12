package com.dresta0056.whowantcoffee.ui.cellar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dresta0056.whowantcoffee.WhoWantCoffeeApplication
import com.dresta0056.whowantcoffee.data.Coffee
import com.dresta0056.whowantcoffee.data.CoffeeRepository
import kotlinx.coroutines.launch

class CellarViewModel(
    private val repository: CoffeeRepository
) : ViewModel() {

    val archivedCoffees = repository.archivedCoffees()

    fun restoreCoffee(id: Int) {
        viewModelScope.launch {
            repository.restore(id)
        }
    }

    fun deleteCoffee(coffee: Coffee) {
        viewModelScope.launch {
            repository.hardDelete(coffee)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WhoWantCoffeeApplication
                CellarViewModel(app.repository)
            }
        }
    }
}