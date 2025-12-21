package com.phoenixalpha.rpgdiceroller.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.phoenixalpha.rpgdiceroller.DiceViewModel
import com.phoenixalpha.rpgdiceroller.data.Die
import kotlinx.coroutines.delay
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

enum class SecondRoll {
    NEITHER,
    ADVANTAGE,
    DISADVANTAGE;

    fun asLowerCase() = name.lowercase()
    fun next() = entries[(ordinal + 1) % entries.size]
}

fun rollDice(state: DiceOptionsState, size: Int) = if (state.individual) {
    List(state.numDice) {
        rollOnce(size, state.advantage) + state.modifier
    }.sortedDescending()
} else {
    listOf(
        List(state.numDice) {
            rollOnce(size, state.advantage)
        }.sum() + state.modifier
    )
}

fun rollOnce(size: Int, advantage: SecondRoll) = when (advantage) {
    SecondRoll.NEITHER -> rollDie(size)
    SecondRoll.ADVANTAGE -> max(rollDie(size), rollDie(size))
    SecondRoll.DISADVANTAGE -> min(rollDie(size), rollDie(size))
}

fun rollDie(size: Int) = Random.nextInt(1, size + 1)

@Composable
fun DiceRoller() {
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

    DiceRollerContent(state, callbacks)
}

@Composable
private fun DiceRollerContent(
    state: DiceOptionsState,
    callbacks: DiceOptionsCallbacks,
    viewModel: DiceViewModel = hiltViewModel()
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val dice = viewModel.dice.collectAsState()

        LazyVerticalGrid(
            GridCells.Adaptive(96.dp),
            Modifier.weight(1f),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dice.value) { DiceCard(state, it) }
            item { AddDiceCard() }
        }
        HorizontalDivider(Modifier.padding(horizontal = 8.dp), 2.dp)
        RollOptions(state, callbacks)
    }
}

@Composable
fun DiceCard(state: DiceOptionsState, size: Int) {
    val result = remember { mutableStateListOf<Int>() }
    var delete by remember { mutableStateOf(false) }

    Card(
        Modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = { result.addAll(rollDice(state, size)) },
                onLongClick = { delete = true }
            )
    ) {
        Text(
            "d$size",
            Modifier
                .fillMaxSize()
                .wrapContentSize(),
            fontSize = 28.sp
        )
    }

    if (result.isNotEmpty()) {
        Dialog({ result.clear() }) { DiceRoll(result) }
    }

    if (delete) {
        Dialog({ delete = false }) {
            DeleteDie(size) { delete = false }
        }
    }
}

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

@Composable
fun DeleteDie(size: Int, viewModel: DiceViewModel = hiltViewModel(), dismiss: () -> Unit) {
    Card {
        Text(
            "Delete this die?",
            Modifier
                .width(225.dp)
                .padding(top = 24.dp, bottom = 36.dp)
                .wrapContentWidth(),
            fontSize = 24.sp
        )
        Row(
            Modifier
                .width(225.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            TextButton(dismiss) {
                Text("Cancel", Modifier.padding(end = 8.dp))
            }
            TextButton({
                viewModel.deleteDie(size)
                dismiss()
            }) { Text("Confirm", Modifier.padding(end = 16.dp)) }
        }
    }
}

@Composable
fun AddDiceCard(viewModel: DiceViewModel = hiltViewModel()) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        Modifier
            .aspectRatio(1f)
            .clickable { showDialog = true }
    ) {
        Text(
            "+",
            Modifier
                .fillMaxSize()
                .wrapContentSize(),
            fontSize = 56.sp
        )
    }

    if (showDialog) {
        NumberDialog(
            rememberTextFieldState(""),
            "Number of sides",
            InputTransformation.byValue { _, proposed ->
                proposed.filter { it.isDigit() }
            },
            { viewModel.addDie(Die(it.coerceAtLeast(2))) }
        ) { showDialog = false }
    }
}

@Composable
fun RollOptions(state: DiceOptionsState, callbacks: DiceOptionsCallbacks) {
    Column(
        Modifier.padding(8.dp),
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
    var showDialog by remember { mutableStateOf(false) }

    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton({ updateNumDice(numDice - 1) }) {
                Text("-", fontSize = 28.sp)
            }
            Text(
                numDice.toString(),
                Modifier.clickable { showDialog = true },
                fontSize = 28.sp
            )
            TextButton({ updateNumDice(numDice + 1) }) {
                Text("+", fontSize = 28.sp)
            }
        }
    }

    if (showDialog) {
        NumberDialog(
            rememberTextFieldState("1"),
            "Number of dice",
            InputTransformation.byValue { _, proposed ->
                proposed.filter { it.isDigit() }
            },
            updateNumDice
        ) { showDialog = false }
    }
}

@Composable
fun Modifier(modifier: Int, updateModifier: (Int) -> Unit) {
    fun transformation() = InputTransformation.byValue { current, proposed ->
        when (proposed.length) {
            0 -> proposed

            1 -> if (proposed.first() == '-' || proposed.isDigitsOnly()) {
                proposed
            } else {
                current
            }

            else -> if (
                proposed.drop(1).isDigitsOnly() &&
                (proposed.first() == '-' || proposed.first().isDigit())
            ) {
                proposed
            } else {
                current
            }
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton({ updateModifier(modifier - 1) }) {
                Text("-", fontSize = 28.sp)
            }
            Text(
                if (modifier >= 0) "+$modifier" else modifier.toString(),
                Modifier.clickable { showDialog = true },
                fontSize = 28.sp
            )
            TextButton({ updateModifier(modifier + 1) }) {
                Text("+", fontSize = 28.sp)
            }
        }
    }

    if (showDialog) {
        NumberDialog(
            rememberTextFieldState("0"),
            "Roll modifier",
            transformation(),
            updateModifier
        ) { showDialog = false }
    }
}

@Composable
fun NumberDialog(
    state: TextFieldState,
    label: String,
    inputTransformation: InputTransformation,
    update: (Int) -> Unit,
    dismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Dialog({ dismiss() }) {
        Card(
            Modifier
                .height(150.dp)
                .width(200.dp)
        ) {
            NumberDialogText(state, focusRequester, label, inputTransformation)
            NumberDialogConfirmation(state, update, dismiss)
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
}

@Composable
private fun NumberDialogText(
    state: TextFieldState,
    focusRequester: FocusRequester,
    label: String,
    inputTransformation: InputTransformation
) {
    OutlinedTextField(
        state,
        Modifier
            .padding(16.dp)
            .focusRequester(focusRequester),
        label = { Text(label) },
        inputTransformation = inputTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        lineLimits = TextFieldLineLimits.SingleLine
    )
}

@Composable
private fun NumberDialogConfirmation(
    state: TextFieldState,
    update: (Int) -> Unit,
    dismiss: () -> Unit
) {
    Box(Modifier.fillMaxWidth(), Alignment.BottomEnd) {
        TextButton({
            val text = state.text
            update(if (text.isEmpty()) 0 else text.toString().toInt())
            dismiss()
        }) { Text("Confirm", Modifier.padding(end = 16.dp)) }
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
