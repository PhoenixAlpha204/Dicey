package com.phoenixalpha.rpgdiceroller.data

import javax.inject.Inject
import kotlinx.coroutines.flow.map

class DiceRepository @Inject constructor(private val dao: DiceDao) {
    suspend fun addResult(result: Result) {
        dao.addResult(result)
    }

    fun getAllResults() = dao.getAllResults()

    suspend fun clearResults() {
        dao.clearResults()
    }

    suspend fun addDie(die: Die) {
        dao.addDie(die)
    }

    fun getAllDice() = dao.getAllDice().map { dice ->
        dice.map { it.sides }
    }

    suspend fun deleteDie(die: Die) {
        dao.deleteDie(die)
    }
}
