package com.phoenixalpha.dicey.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Die(@PrimaryKey val sides: Int)
