package com.dresta0056.whowantcoffee.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dresta0056.whowantcoffee.R
import com.dresta0056.whowantcoffee.WhoWantCoffeeApplication
import com.dresta0056.whowantcoffee.data.Coffee
import com.dresta0056.whowantcoffee.data.CoffeeRepository
import com.dresta0056.whowantcoffee.nav.KEY_ID_COFFEE
import com.dresta0056.whowantcoffee.util.ratingStars
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CoffeeDetailViewModel(
    private val repository: CoffeeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Int? = savedStateHandle[KEY_ID_COFFEE]

    val isEditMode: Boolean = id != null

    private val _currentCoffee = MutableStateFlow<Coffee?>(null)
    val currentCoffee: StateFlow<Coffee?> = _currentCoffee

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _process = MutableStateFlow("")
    val process: StateFlow<String> = _process

    private val _rating = MutableStateFlow(3)
    val rating: StateFlow<Int> = _rating

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    private val _snackbarMessageRes = MutableStateFlow<Int?>(null)
    val snackbarMessageRes: StateFlow<Int?> = _snackbarMessageRes

    private val _snackbarMessageArg = MutableStateFlow<String?>(null)
    val snackbarMessageArg: StateFlow<String?> = _snackbarMessageArg

    init {
        if (id != null) {
            viewModelScope.launch {
                val coffee = repository.getById(id).first()
                _currentCoffee.value = coffee

                if (coffee != null) {
                    _name.value = coffee.name
                    _process.value = coffee.process
                    _rating.value = coffee.rating
                    _notes.value = coffee.notes.orEmpty()
                }
            }
        }
    }

    fun updateName(value: String) {
        _name.value = value
    }

    fun updateProcess(value: String) {
        _process.value = value
    }

    fun updateRating(value: Int) {
        _rating.value = value.coerceIn(1, 5)
    }

    fun updateNotes(value: String) {
        _notes.value = value
    }

    fun isInputValid(): Boolean {
        return _name.value.isNotBlank() &&
                _process.value.isNotBlank() &&
                _rating.value in 1..5
    }

    fun save() {
        if (!isInputValid()) return

        viewModelScope.launch {
            if (isEditMode) {
                val oldCoffee = _currentCoffee.value ?: return@launch

                repository.updateCoffee(
                    oldCoffee.copy(
                        name = _name.value.trim(),
                        process = _process.value,
                        rating = _rating.value,
                        notes = _notes.value.trim().ifBlank { null }
                    )
                )

                _snackbarMessageRes.value = R.string.snackbar_saved
                _snackbarMessageArg.value = null
            } else {
                repository.addCoffee(
                    name = _name.value.trim(),
                    process = _process.value,
                    rating = _rating.value,
                    notes = _notes.value.trim().ifBlank { null }
                )

                _snackbarMessageRes.value = R.string.snackbar_added
                _snackbarMessageArg.value = _name.value.trim()
            }

            _isFinished.value = true
        }
    }

    fun archiveCoffee() {
        val coffeeId = id ?: return

        viewModelScope.launch {
            repository.archive(coffeeId)
            _snackbarMessageRes.value = R.string.snackbar_archived
            _snackbarMessageArg.value = _name.value.trim()
            _isFinished.value = true
        }
    }

    fun undoArchive() {
        val coffeeId = id ?: return

        viewModelScope.launch {
            repository.restore(coffeeId)
        }
    }

    fun deleteCoffee() {
        val coffee = _currentCoffee.value ?: return

        viewModelScope.launch {
            repository.hardDelete(coffee)
            _snackbarMessageRes.value = R.string.snackbar_deleted
            _snackbarMessageArg.value = coffee.name
            _isFinished.value = true
        }
    }

    fun shareText(localizedProcess: String): String {
        return "${_name.value} ($localizedProcess)\n${ratingStars(_rating.value)}\n${_notes.value}"
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as WhoWantCoffeeApplication
                val savedStateHandle = createSavedStateHandle()

                CoffeeDetailViewModel(
                    repository = app.repository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}