package com.phoenixalpha.dicey.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class HelpEntry(val header: String, val content: String)

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

    LazyColumn(
        Modifier.padding(12.dp, 12.dp, 12.dp)
    ) {
        item { About() }
        helpEntries.forEach { (header, content) ->
            stickyHeader { Header(header) }
            item { Content(content) }
        }
        item { Spacer(Modifier.padding(bottom = 12.dp)) }
    }
}

@Composable
private fun About() {
    val uriHandler = LocalUriHandler.current
    val repoUrl = "https://github.com/PhoenixAlpha204/Dicey"

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Dicey",
            Modifier.padding(vertical = 20.dp),
            style = typography.displayLarge
        )
        Button({ uriHandler.openUri(repoUrl) }) { Text("Source Code") }
        Text("ⓒ PhoenixAlpha", fontSize = 12.sp)
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
