package com.phoenixalpha.rpgdiceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phoenixalpha.rpgdiceroller.ui.theme.RPGDiceRollerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPGDiceRollerTheme {
                Scaffold { innerPadding -> DiceRoller(innerPadding) }
            }
        }
    }
}

@Composable
fun DiceRoller(innerPadding: PaddingValues) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyVerticalGrid(
            GridCells.Adaptive(96.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listOf(4, 6, 8, 10, 12, 20, 100)) {
                DiceCard(it)
            }
        }
        RollOptions()
    }
}

@Composable
fun DiceCard(size: Int) {
    Card(Modifier.aspectRatio(1f)) {
        Text(
            "d$size",
            Modifier
                .fillMaxSize()
                .wrapContentSize(),
            fontSize = 28.sp
        )
    }
}

enum class SecondRoll {
    NEITHER,
    ADVANTAGE,
    DISADVANTAGE;

    fun asLowerCase(): String = name.lowercase()
    fun next(): SecondRoll = entries[(ordinal + 1) % entries.size]
}

@Composable
fun RollOptions() {
    var numDice by remember { mutableIntStateOf(1) }
    var modifier by remember { mutableIntStateOf(0) }
    var individual by remember { mutableStateOf(false) }
    var advantage by remember { mutableStateOf(SecondRoll.NEITHER) }

    Column(
        Modifier.padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (individual || numDice == 1) {
            Advantage(advantage) { advantage = advantage.next() }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DiceNumber(numDice) {
                numDice = it.coerceAtLeast(1)
                if (!individual && numDice > 1) advantage = SecondRoll.NEITHER
            }
            Modifier(modifier) { modifier = it }
        }
        IndividualOrCombined(individual) {
            individual = !individual
            if (!individual && numDice > 1) advantage = SecondRoll.NEITHER
        }
    }
}

@Composable
fun Advantage(advantage: SecondRoll, next: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Roll with: ", fontSize = 20.sp)
        Card(Modifier.clickable { next() }) {
            Text(
                advantage.asLowerCase(),
                Modifier.padding(8.dp),
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun DiceNumber(numDice: Int, updateNumDice: (Int) -> Unit) {
    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton({ updateNumDice(numDice - 1) }) {
                Text("-", fontSize = 28.sp)
            }
            Text(
                numDice.toString(),
                Modifier.clickable { updateNumDice(1) },
                fontSize = 28.sp
            )
            TextButton({ updateNumDice(numDice + 1) }) {
                Text("+", fontSize = 28.sp)
            }
        }
    }
}

@Composable
fun Modifier(modifier: Int, updateModifier: (Int) -> Unit) {
    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton({ updateModifier(modifier - 1) }) {
                Text("-", fontSize = 28.sp)
            }
            Text(
                if (modifier >= 0) "+$modifier" else modifier.toString(),
                Modifier.clickable { updateModifier(0) },
                fontSize = 28.sp
            )
            TextButton({ updateModifier(modifier + 1) }) {
                Text("+", fontSize = 28.sp)
            }
        }
    }
}

@Composable
fun IndividualOrCombined(individual: Boolean, toggleIndividual: () -> Unit) {
    Card(Modifier.clickable { toggleIndividual() }) {
        Text(
            if (individual) {
                "Roll each die separately"
            } else {
                "Add dice together"
            },
            Modifier.padding(8.dp),
            fontSize = 20.sp
        )
    }
}
