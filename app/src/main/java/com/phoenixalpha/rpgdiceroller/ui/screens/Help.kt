package com.phoenixalpha.rpgdiceroller.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HelpEntry(val header: String, val content: String)

@Composable
fun Help() {
    val helpEntries = listOf(
        HelpEntry(
            "Add Together/Roll Separately",
            """
                "Add dice together" rolls the dice, adds them together, and adds the modifier once.
                
                "Roll dice separately" treats each die as an individual roll, adding the modifier to each. Useful for rolling for swarms of creatures.
            """.trimIndent()
        ),
        HelpEntry(
            "Advantage/Disadvantage",
            "Roll twice and take the higher/lower of the two respectively. When used with \"Roll dice separately\", it is applied to each roll."
        ),
        HelpEntry(
            "Custom Dice",
            "Tap the + button to add a die with a custom number of sides. Tap and hold a die to delete it."
        )
    )

    LazyColumn(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        helpEntries.forEach { (header, content) ->
            stickyHeader { Header(header) }
            item { Content(content) }
        }
    }
}

@Composable
private fun Header(header: String) {
    Surface {
        Column {
            Text(header, style = typography.displaySmall)
            HorizontalDivider(Modifier.padding(top = 10.dp), thickness = 2.dp)
        }
    }
}

@Composable
private fun Content(content: String) {
    Text(
        content,
        Modifier.padding(vertical = 20.dp),
        fontSize = 18.sp
    )
}
