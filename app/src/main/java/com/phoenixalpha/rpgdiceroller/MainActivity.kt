package com.phoenixalpha.rpgdiceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.phoenixalpha.rpgdiceroller.ui.screens.DiceRoller
import com.phoenixalpha.rpgdiceroller.ui.theme.RPGDiceRollerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
private data object Home : NavKey

@Serializable
private data object History : NavKey

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = rememberNavBackStack(Home)

            RPGDiceRollerTheme {
                Scaffold(bottomBar = { NavBar(backStack) }) { innerPadding ->
                    MainContent(backStack, innerPadding)
                }
            }
        }
    }
}

@Composable
private fun NavBar(backStack: NavBackStack<NavKey>) {
    fun onClick(item: NavKey) {
        if (backStack.last() != Home) backStack.removeLastOrNull()
        if (item != Home) backStack.add(item)
    }

    NavigationBar {
        listOf(Home, History).forEach {
            NavigationBarItem(
                it == backStack.last(),
                { onClick(it) },
                { Text(it.toString()) }
            )
        }
    }
}

@Composable
private fun MainContent(backStack: NavBackStack<NavKey>, innerPadding: PaddingValues) {
    val animation = ContentTransform(
        fadeIn(tween(300)),
        fadeOut(tween(300))
    )

    NavDisplay(
        backStack,
        Modifier.padding(innerPadding),
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Home> { DiceRoller() }
            entry<History> { }
        },
        transitionSpec = { animation },
        popTransitionSpec = { animation }
    )
}
