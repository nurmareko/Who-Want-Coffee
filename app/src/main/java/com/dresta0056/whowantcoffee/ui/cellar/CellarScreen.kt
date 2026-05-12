package com.dresta0056.whowantcoffee.ui.cellar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dresta0056.whowantcoffee.data.Coffee
import com.dresta0056.whowantcoffee.util.ratingStars
import com.dresta0056.whowantcoffee.util.relativeDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellarScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
    viewModel: CellarViewModel = viewModel(factory = CellarViewModel.Factory)
) {
    val coffees by viewModel.archivedCoffees.collectAsState(initial = emptyList())

    var coffeeToDelete by remember { mutableStateOf<Coffee?>(null) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Cellar",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (coffees.isEmpty()) {
            EmptyCellar(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Coffees you've set aside.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(coffees) { coffee ->
                    CellarCard(
                        coffee = coffee,
                        onRestore = {
                            viewModel.restoreCoffee(coffee.id)

                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    "'${coffee.name}' restored."
                                )
                            }
                        },
                        onDelete = {
                            coffeeToDelete = coffee
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (coffeeToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                coffeeToDelete = null
            },
            title = {
                Text("Delete '${coffeeToDelete?.name}'?")
            },
            text = {
                Text("Removing this from your log. No coming back.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coffeeToDelete?.let { coffee ->
                            viewModel.deleteCoffee(coffee)

                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    "'${coffee.name}' deleted."
                                )
                            }
                        }

                        coffeeToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        coffeeToDelete = null
                    }
                ) {
                    Text("Keep it")
                }
            }
        )
    }
}

@Composable
private fun EmptyCellar(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocalCafe,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Cellar's empty.",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Coffees you archive will land here.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CellarCard(
    coffee: Coffee,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coffee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val archivedText = coffee.archivedAt?.let {
                    "Archived ${relativeDate(it)}"
                } ?: "Archived"

                Text(
                    text = archivedText,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "${coffee.process} · ${ratingStars(coffee.rating)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row {
                IconButton(
                    onClick = onRestore
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore"
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete forever",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}