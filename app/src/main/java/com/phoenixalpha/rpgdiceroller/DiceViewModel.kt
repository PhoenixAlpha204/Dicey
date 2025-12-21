package com.phoenixalpha.rpgdiceroller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenixalpha.rpgdiceroller.data.DiceRepository
import com.phoenixalpha.rpgdiceroller.data.Die
import com.phoenixalpha.rpgdiceroller.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiceViewModel @Inject constructor(private val repository: DiceRepository) : ViewModel() {
    val results = repository.getAllResults().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val dice = repository.getAllDice().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addResult(result: Result) {
        viewModelScope.launch { repository.addResult(result) }
    }

    fun clearResults() {
        viewModelScope.launch { repository.clearResults() }
    }

    fun addDie(die: Die) {
        viewModelScope.launch { repository.addDie(die) }
    }

    fun deleteDie(sides: Int) {
        viewModelScope.launch { repository.deleteDie(sides) }
    }
}
