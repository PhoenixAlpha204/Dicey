package com.phoenixalpha.rpgdiceroller.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
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
import com.phoenixalpha.rpgdiceroller.DiceViewModel

@Composable
fun History(viewModel: DiceViewModel = hiltViewModel()) {
    val results = viewModel.results.collectAsState()

    LazyColumn(reverseLayout = true) {
        item { Spacer(Modifier.padding(bottom = 60.dp)) }
        items(results.value) {
            Text(it.toString(), Modifier.padding(vertical = 20.dp))
        }
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
