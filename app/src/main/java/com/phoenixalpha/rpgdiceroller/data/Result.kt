package com.phoenixalpha.rpgdiceroller.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Result(
    @PrimaryKey(true) val id: Int,
    @ColumnInfo("dice_rolled") val diceRolled: String,
    val result: String,
    @ColumnInfo("created_at") val createdAt: Long
)
