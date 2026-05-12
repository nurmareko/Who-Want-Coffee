package com.dresta0056.whowantcoffee.ui.coffeelist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dresta0056.whowantcoffee.R
import com.dresta0056.whowantcoffee.data.Coffee
import com.dresta0056.whowantcoffee.nav.Screen
import com.dresta0056.whowantcoffee.util.getProcessDisplayName
import com.dresta0056.whowantcoffee.util.ratingStars
import com.dresta0056.whowantcoffee.util.relativeDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeListScreen(
    navController: NavHostController,
    viewModel: CoffeeListViewModel = viewModel(factory = CoffeeListViewModel.Factory)
) {
    val coffees by viewModel.coffees.collectAsState(initial = emptyList())
    val sortOrder by viewModel.sortOrder.collectAsState(initial = "recent")
    val viewMode by viewModel.viewMode.collectAsState(initial = "list")
    val archivedCount by viewModel.archivedCount.collectAsState(initial = 0)

    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Screen.CoffeeAdd.route)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_coffee)
                    )
                },
                text = {
                    Text(stringResource(R.string.add_coffee))
                },
                elevation = FloatingActionButtonDefaults.elevation()
            )
        },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.coffee_list_title),
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleSort(sortOrder)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = stringResource(R.string.cd_toggle_sort)
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.toggleView(viewMode)
                        }
                    ) {
                        Icon(
                            imageVector = if (viewMode == "list") {
                                Icons.Default.GridView
                            } else {
                                Icons.AutoMirrored.Filled.List
                            },
                            contentDescription = stringResource(R.string.cd_toggle_view)
                        )
                    }

                    IconButton(
                        onClick = {
                            menuExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.cd_menu)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (archivedCount > 0) {
                                        stringResource(R.string.cellar_with_count, archivedCount)
                                    } else {
                                        stringResource(R.string.cellar)
                                    }
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.Cellar.route)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.about)) },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.About.route)
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (coffees.isEmpty()) {
            EmptyCoffeeList(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            if (viewMode == "list") {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(coffees) { coffee ->
                        CoffeeCard(
                            coffee = coffee,
                            onClick = {
                                navController.navigate(Screen.CoffeeEdit.withId(coffee.id))
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(96.dp))
                    }
                }
            } else {
                CoffeeGrid(
                    coffees = coffees,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    onCoffeeClick = { coffee ->
                        navController.navigate(Screen.CoffeeEdit.withId(coffee.id))
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyCoffeeList(
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
            text = stringResource(R.string.no_coffees_logged),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.tap_add_first),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CoffeeCard(
    coffee: Coffee,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coffee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${getProcessDisplayName(context, coffee.process)} · ${ratingStars(coffee.rating)}",
                    style = MaterialTheme.typography.bodySmall
                )

                if (!coffee.notes.isNullOrBlank()) {
                    Text(
                        text = coffee.notes,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = relativeDate(context, coffee.dateAdded),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoffeeGrid(
    coffees: List<Coffee>,
    modifier: Modifier = Modifier,
    onCoffeeClick: (Coffee) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(coffees) { coffee ->
            GridCoffeeCard(
                coffee = coffee,
                onClick = {
                    onCoffeeClick(coffee)
                }
            )
        }
    }
}

@Composable
private fun GridCoffeeCard(
    coffee: Coffee,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = coffee.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = getProcessDisplayName(context, coffee.process),
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = ratingStars(coffee.rating),
                style = MaterialTheme.typography.bodySmall
            )

            if (!coffee.notes.isNullOrBlank()) {
                Text(
                    text = coffee.notes,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}
