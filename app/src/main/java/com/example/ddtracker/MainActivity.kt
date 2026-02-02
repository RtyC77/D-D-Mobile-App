package com.example.ddtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                TrackerScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen() {
    var playerName by remember { mutableStateOf("") }
    var currentHp by remember { mutableIntStateOf(12) }
    var maxHp by remember { mutableIntStateOf(12) }
    var potionSlots by remember { mutableIntStateOf(3) }
    var potionsUsed by remember { mutableIntStateOf(0) }

    val abilities = remember {
        listOf(
            AbilityState("Força", 14),
            AbilityState("Destreza", 12),
            AbilityState("Constituição", 13),
            AbilityState("Inteligência", 10),
            AbilityState("Sabedoria", 11),
            AbilityState("Carisma", 9)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "D&D 5e - Rastreador de Personagem",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Jogador", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Pontos de Vida", style = MaterialTheme.typography.titleMedium)
                StatStepper(
                    label = "Atual",
                    value = currentHp,
                    onDecrease = { if (currentHp > 0) currentHp -= 1 },
                    onIncrease = { if (currentHp < maxHp) currentHp += 1 }
                )
                StatStepper(
                    label = "Máximo",
                    value = maxHp,
                    onDecrease = {
                        if (maxHp > 1) {
                            maxHp -= 1
                            if (currentHp > maxHp) currentHp = maxHp
                        }
                    },
                    onIncrease = { maxHp += 1 }
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Poções", style = MaterialTheme.typography.titleMedium)
                StatStepper(
                    label = "Slots",
                    value = potionSlots,
                    onDecrease = {
                        if (potionSlots > 0) {
                            potionSlots -= 1
                            if (potionsUsed > potionSlots) potionsUsed = potionSlots
                        }
                    },
                    onIncrease = { potionSlots += 1 }
                )
                StatStepper(
                    label = "Usadas",
                    value = potionsUsed,
                    onDecrease = { if (potionsUsed > 0) potionsUsed -= 1 },
                    onIncrease = { if (potionsUsed < potionSlots) potionsUsed += 1 }
                )
                Text(
                    text = "Restantes: ${potionSlots - potionsUsed}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Habilidades", style = MaterialTheme.typography.titleMedium)
                abilities.forEach { ability ->
                    AbilityRow(ability = ability)
                }
            }
        }
    }
}

@Composable
fun StatStepper(
    label: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onDecrease) {
                Text(text = "-")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = value.toString(), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = onIncrease) {
                Text(text = "+")
            }
        }
    }
}

@Composable
fun AbilityRow(ability: AbilityState) {
    var value by remember { mutableIntStateOf(ability.value) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = ability.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Modificador: ${modifierFromScore(value)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (value > 1) value -= 1 }) {
                Text(text = "-")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = value.toString(), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = { value += 1 }) {
                Text(text = "+")
            }
        }
    }
}

data class AbilityState(val name: String, val value: Int)

fun modifierFromScore(score: Int): String {
    val modifier = (score - 10) / 2
    return if (modifier >= 0) "+$modifier" else modifier.toString()
}
