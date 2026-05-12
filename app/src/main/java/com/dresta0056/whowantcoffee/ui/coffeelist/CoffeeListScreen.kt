package com.dresta0056.whowantcoffee.ui.coffeelist

import android.content.res.Configuration
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import com.dresta0056.whowantcoffee.ui.theme.WhoWantCoffeeTheme
import com.dresta0056.whowantcoffee.R
import com.dresta0056.whowantcoffee.data.Coffee
import com.dresta0056.whowantcoffee.nav.Screen
import com.dresta0056.whowantcoffee.util.getProcessDisplayNameRes
import com.dresta0056.whowantcoffee.util.ratingStars
import com.dresta0056.whowantcoffee.util.relativeDateResource

@Composable
fun CoffeeListScreen(
    navController: NavHostController,
    viewModel: CoffeeListViewModel = viewModel(factory = CoffeeListViewModel.Factory)
) {
    val coffees by viewModel.coffees.collectAsState(initial = emptyList())
    val sortOrder by viewModel.sortOrder.collectAsState(initial = "recent")
    val viewMode by viewModel.viewMode.collectAsState(initial = "list")
    val archivedCount by viewModel.archivedCount.collectAsState(initial = 0)

    CoffeeListContent(
        coffees = coffees,
        sortOrder = sortOrder,
        viewMode = viewMode,
        archivedCount = archivedCount,
        onAddClick = { navController.navigate(Screen.CoffeeAdd.route) },
        onToggleSort = { viewModel.toggleSort(sortOrder) },
        onToggleView = { viewModel.toggleView(viewMode) },
        onCellarClick = { navController.navigate(Screen.Cellar.route) },
        onAboutClick = { navController.navigate(Screen.About.route) },
        onCoffeeClick = { coffee ->
            navController.navigate(Screen.CoffeeEdit.withId(coffee.id))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoffeeListContent(
    coffees: List<Coffee>,
    sortOrder: String,
    viewMode: String,
    archivedCount: Int,
    onAddClick: () -> Unit,
    onToggleSort: () -> Unit,
    onToggleView: () -> Unit,
    onCellarClick: () -> Unit,
    onAboutClick: () -> Unit,
    onCoffeeClick: (Coffee) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_coffee)
                    )
                },
                text = {
                    Text(stringResource(R.string.add_coffee))
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(
                        onClick = onToggleSort
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = stringResource(R.string.cd_toggle_sort)
                        )
                    }

                    IconButton(
                        onClick = onToggleView
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
                                onCellarClick()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.about)) },
                            onClick = {
                                menuExpanded = false
                                onAboutClick()
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
                                onCoffeeClick(coffee)
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
                    onCoffeeClick = onCoffeeClick
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
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.tap_add_first),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CoffeeCard(
    coffee: Coffee,
    onClick: () -> Unit
) {
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
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${getProcessDisplayNameRes(coffee.process)?.let { stringResource(it) } ?: coffee.process} · ${ratingStars(coffee.rating)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!coffee.notes.isNullOrBlank()) {
                    Text(
                        text = coffee.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = relativeDateResource(coffee.dateAdded),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = getProcessDisplayNameRes(coffee.process)?.let { stringResource(it) } ?: coffee.process,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = ratingStars(coffee.rating),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!coffee.notes.isNullOrBlank()) {
                Text(
                    text = coffee.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun CoffeeListPreview() {
    WhoWantCoffeeTheme {
        CoffeeListContent(
            coffees = listOf(
                Coffee(
                    id = 1,
                    name = "Ethiopia Yirgacheffe",
                    process = "Washed",
                    rating = 5,
                    notes = "Floral, Citrus, Bergamot",
                    dateAdded = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                ),
                Coffee(
                    id = 2,
                    name = "Colombia Huila",
                    process = "Natural",
                    rating = 4,
                    notes = "Chocolate, Red berries",
                    dateAdded = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                ),
                Coffee(
                    id = 3,
                    name = "Brazil Minas Gerais",
                    process = "Natural",
                    rating = 3,
                    notes = "Nutty, Caramel",
                    dateAdded = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                )
            ),
            sortOrder = "recent",
            viewMode = "list",
            archivedCount = 2,
            onAddClick = {},
            onToggleSort = {},
            onToggleView = {},
            onCellarClick = {},
            onAboutClick = {},
            onCoffeeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun CoffeeGridPreview() {
    WhoWantCoffeeTheme {
        CoffeeListContent(
            coffees = listOf(
                Coffee(
                    id = 1,
                    name = "Ethiopia Yirgacheffe",
                    process = "Washed",
                    rating = 5,
                    notes = "Floral, Citrus, Bergamot",
                    dateAdded = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                ),
                Coffee(
                    id = 2,
                    name = "Colombia Huila",
                    process = "Natural",
                    rating = 4,
                    notes = "Chocolate, Red berries",
                    dateAdded = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                ),
                Coffee(
                    id = 3,
                    name = "Brazil Minas Gerais",
                    process = "Natural",
                    rating = 3,
                    notes = "Nutty, Caramel",
                    dateAdded = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                )
            ),
            sortOrder = "recent",
            viewMode = "grid",
            archivedCount = 2,
            onAddClick = {},
            onToggleSort = {},
            onToggleView = {},
            onCellarClick = {},
            onAboutClick = {},
            onCoffeeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun EmptyCoffeeListPreview() {
    WhoWantCoffeeTheme {
        CoffeeListContent(
            coffees = emptyList(),
            sortOrder = "recent",
            viewMode = "list",
            archivedCount = 0,
            onAddClick = {},
            onToggleSort = {},
            onToggleView = {},
            onCellarClick = {},
            onAboutClick = {},
            onCoffeeClick = {}
        )
    }
}
