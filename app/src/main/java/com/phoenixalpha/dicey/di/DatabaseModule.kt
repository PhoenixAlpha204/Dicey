package com.phoenixalpha.dicey.di

import android.content.Context
import androidx.room.Room
import com.phoenixalpha.dicey.data.DiceDao
import com.phoenixalpha.dicey.data.DiceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDiceDatabase(@ApplicationContext context: Context): DiceDatabase = Room
        .databaseBuilder(context, DiceDatabase::class.java, "Results.db")
        .createFromAsset("dice.db")
        .build()

    @Provides
    fun provideDiceDao(database: DiceDatabase): DiceDao = database.diceDao()
}
