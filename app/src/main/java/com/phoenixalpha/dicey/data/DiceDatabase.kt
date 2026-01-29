package com.phoenixalpha.dicey.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Result::class, Die::class], version = 1)
abstract class DiceDatabase : RoomDatabase() {
    abstract fun diceDao(): DiceDao
}
