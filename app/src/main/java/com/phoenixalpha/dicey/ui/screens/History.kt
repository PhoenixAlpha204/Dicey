package com.phoenixalpha.dicey.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.phoenixalpha.dicey.DiceViewModel
import com.phoenixalpha.dicey.data.Result

@Composable
fun History(viewModel: DiceViewModel = hiltViewModel()) {
    fun calculateShape(index: Int, lastIndex: Int): RoundedCornerShape {
        val outerRadius = 15.dp
        val innerRadius = 5.dp

        return when (index) {
            lastIndex -> RoundedCornerShape(
                outerRadius,
                outerRadius,
                innerRadius,
                innerRadius
            )

            0 -> RoundedCornerShape(
                innerRadius,
                innerRadius,
                outerRadius,
                outerRadius
            )

            else -> RoundedCornerShape(innerRadius)
        }
    }

    val results = viewModel.results.collectAsState().value

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        item { Spacer(Modifier.padding(bottom = 65.dp)) }
        itemsIndexed(results) { index, item ->
            val lastIndex = results.lastIndex
            val shape = calculateShape(index, lastIndex)

            HistoryItem(shape, item)
        }
    }
}

@Composable
private fun HistoryItem(shape: RoundedCornerShape, item: Result) {
    Surface(shape = shape) {
        ListItem(
            { Text(item.result) },
            supportingContent = { Text(item.diceRolled) },
            colors = ListItemDefaults.colors(
                MaterialTheme.colorScheme.surfaceContainerHigh
            )
        )
    }
}

@Composable
fun ClearHistoryButton(viewModel: DiceViewModel = hiltViewModel()) {
    var initialVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { initialVisible = true }

    AnimatedVisibility(initialVisible, enter = fadeIn()) {
        FloatingActionButton({ viewModel.clearResults() }) {
            Text(
                "Clear History",
                Modifier.padding(12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
