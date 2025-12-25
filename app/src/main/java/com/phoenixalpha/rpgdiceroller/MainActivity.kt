package com.phoenixalpha.rpgdiceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.phoenixalpha.rpgdiceroller.ui.screens.ClearHistoryButton
import com.phoenixalpha.rpgdiceroller.ui.screens.DiceRoller
import com.phoenixalpha.rpgdiceroller.ui.screens.Help
import com.phoenixalpha.rpgdiceroller.ui.screens.History
import com.phoenixalpha.rpgdiceroller.ui.theme.RPGDiceRollerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
private data object Home : NavKey

@Serializable
private data object History : NavKey

@Serializable
private data object Help : NavKey

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = rememberNavBackStack(Home)

            RPGDiceRollerTheme {
                Scaffold(
                    bottomBar = { NavBar(backStack) },
                    floatingActionButton = { FAB(backStack) }
                ) { innerPadding ->
                    MainContent(backStack, innerPadding)
                }
            }
        }
    }
}

@Composable
private fun NavBar(backStack: NavBackStack<NavKey>) {
    fun navigate(item: NavKey) {
        if (backStack.last() != Home) backStack.removeLastOrNull()
        if (item != Home) backStack.add(item)
    }

    NavigationBar {
        listOf(Home, History, Help).forEach {
            NavigationBarItem(
                it == backStack.last(),
                { navigate(it) },
                { Text(it.toString()) }
            )
        }
    }
}

@Composable
private fun FAB(backStack: NavBackStack<NavKey>) {
    if (backStack.last() == History) ClearHistoryButton()
}

@Composable
private fun MainContent(backStack: NavBackStack<NavKey>, innerPadding: PaddingValues) {
    val animation = ContentTransform(fadeIn(), fadeOut())

    NavDisplay(
        backStack,
        Modifier.padding(innerPadding),
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Home> { DiceRoller() }
            entry<History> { History() }
            entry<Help> { Help() }
        },
        transitionSpec = { animation },
        popTransitionSpec = { animation }
    )
}
