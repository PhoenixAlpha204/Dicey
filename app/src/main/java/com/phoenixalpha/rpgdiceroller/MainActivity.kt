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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.phoenixalpha.rpgdiceroller.ui.theme.RPGDiceRollerTheme
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class DiceOptionsState(
    val numDice: Int = 1,
    val modifier: Int = 0,
    val individual: Boolean = false,
    val advantage: SecondRoll = SecondRoll.NEITHER
)

data class DiceOptionsCallbacks(
    val setNumDice: (Int) -> Unit,
    val setModifier: (Int) -> Unit,
    val toggleIndividual: () -> Unit,
    val nextAdvantage: () -> Unit
)

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
    var state by remember { mutableStateOf(DiceOptionsState()) }
    val callbacks = remember(state) {
        fun newNumDice(state: DiceOptionsState, numDice: Int): DiceOptionsState = state.copy(
            numDice = numDice.coerceAtLeast(1),
            advantage = if (!state.individual && numDice > 1) {
                SecondRoll.NEITHER
            } else {
                state.advantage
            }
        )

        fun toggleIndividual(state: DiceOptionsState): DiceOptionsState = state.copy(
            individual = !state.individual,
            advantage = if (state.individual && state.numDice > 1) {
                SecondRoll.NEITHER
            } else {
                state.advantage
            }
        )

        DiceOptionsCallbacks(
            { state = newNumDice(state, it) },
            { state = state.copy(modifier = it) },
            { state = toggleIndividual(state) },
            { state = state.copy(advantage = state.advantage.next()) }
        )
    }

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
                DiceCard(state, it)
            }
        }
        RollOptions(state, callbacks)
    }
}

@Composable
fun DiceCard(state: DiceOptionsState, size: Int) {
    val result = remember { mutableStateListOf<Int>() }

    Card(
        Modifier
            .aspectRatio(1f)
            .clickable { result.addAll(rollDice(state, size)) }
    ) {
        Text(
            "d$size",
            Modifier
                .fillMaxSize()
                .wrapContentSize(),
            fontSize = 28.sp
        )
    }

    if (!result.isEmpty()) {
        Dialog({ result.clear() }) { DiceRoll(result) }
    }
}

fun rollDice(state: DiceOptionsState, size: Int): List<Int> = if (state.individual) {
    List(state.numDice) {
        rollOnce(size, state.modifier, state.advantage)
    }.sortedDescending()
} else {
    listOf(
        List(state.numDice) {
            rollOnce(size, state.modifier, state.advantage)
        }.sum()
    )
}

fun rollOnce(size: Int, modifier: Int, advantage: SecondRoll): Int = when (advantage) {
    SecondRoll.NEITHER -> rollDie(size)
    SecondRoll.ADVANTAGE -> max(rollDie(size), rollDie(size))
    SecondRoll.DISADVANTAGE -> min(rollDie(size), rollDie(size))
} + modifier

fun rollDie(size: Int): Int = Random.nextInt(1, size + 1)

@Composable
fun DiceRoll(result: List<Int>) {
    Card(
        Modifier
            .widthIn(200.dp)
            .heightIn(200.dp)
    ) {
        Text(
            result.joinToString(),
            Modifier
                .widthIn(200.dp)
                .heightIn(200.dp)
                .padding(16.dp)
                .wrapContentSize(),
            fontSize = 56.sp,
            lineHeight = 79.sp
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
fun RollOptions(state: DiceOptionsState, callbacks: DiceOptionsCallbacks) {
    Column(
        Modifier.padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.individual || state.numDice == 1) {
            Advantage(state.advantage, callbacks.nextAdvantage)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DiceNumber(state.numDice) { callbacks.setNumDice(it) }
            Modifier(state.modifier, callbacks.setModifier)
        }
        IndividualOrCombined(state.individual) { callbacks.toggleIndividual() }
    }
}

@Composable
fun Advantage(advantage: SecondRoll, next: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
