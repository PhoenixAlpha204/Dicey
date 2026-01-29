package com.phoenixalpha.dicey.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiceDao {
    @Insert
    suspend fun addResult(result: Result)

    @Query("SELECT * FROM Result")
    fun getAllResults(): Flow<List<Result>>

    @Query("DELETE FROM Result")
    suspend fun clearResults()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDie(die: Die)

    @Query("SELECT * FROM Die")
    fun getAllDice(): Flow<List<Die>>

    @Delete
    suspend fun deleteDie(die: Die)
}
