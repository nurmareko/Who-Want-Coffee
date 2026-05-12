package com.dresta0056.whowantcoffee.ui.detail

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dresta0056.whowantcoffee.R
import com.dresta0056.whowantcoffee.util.getProcessDisplayName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeDetailScreen(
    navController: NavHostController,
    id: Int? = null,
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
    viewModel: CoffeeDetailViewModel = viewModel(factory = CoffeeDetailViewModel.Factory)
) {
    val context = LocalContext.current

    val name by viewModel.name.collectAsState()
    val process by viewModel.process.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    val snackbarMessageRes by viewModel.snackbarMessageRes.collectAsState()
    val snackbarMessageArg by viewModel.snackbarMessageArg.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }
    var processExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val processes = listOf("Washed", "Honey", "Natural")

    LaunchedEffect(isFinished) {
        if (isFinished) {
            navController.popBackStack()

            snackbarScope.launch {
                snackbarMessageRes?.let { resId ->
                    val message = if (snackbarMessageArg != null) {
                        context.getString(resId, snackbarMessageArg)
                    } else {
                        context.getString(resId)
                    }

                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = if (resId == R.string.snackbar_archived) {
                            context.getString(R.string.undo)
                        } else {
                            null
                        }
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoArchive()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isEditMode) {
                            stringResource(R.string.edit_coffee_title)
                        } else {
                            stringResource(R.string.add_coffee_title)
                        },
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
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    if (viewModel.isEditMode) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        viewModel.shareText(getProcessDisplayName(context, process))
                                    )
                                }

                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        context.getString(R.string.share_coffee)
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.cd_share_coffee)
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
                                text = { Text(stringResource(R.string.archive)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.archiveCoffee()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.delete),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.coffee_name)) },
                isError = name.isBlank(),
                supportingText = {
                    if (name.isBlank()) {
                        Text(stringResource(R.string.validation_name_empty))
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = processExpanded,
                onExpandedChange = {
                    processExpanded = !processExpanded
                }
            ) {
                OutlinedTextField(
                    value = getProcessDisplayName(context, process),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.process)) },
                    isError = process.isBlank(),
                    supportingText = {
                        if (process.isBlank()) {
                            Text(stringResource(R.string.validation_process_empty))
                        }
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = processExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = processExpanded,
                    onDismissRequest = {
                        processExpanded = false
                    }
                ) {
                    processes.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(getProcessDisplayName(context, item)) },
                            onClick = {
                                viewModel.updateProcess(item)
                                processExpanded = false
                            }
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.rating),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row {
                    for (star in 1..5) {
                        IconButton(
                            onClick = {
                                viewModel.updateRating(star)
                            }
                        ) {
                            Icon(
                                imageVector = if (star <= rating) {
                                    Icons.Default.Star
                                } else {
                                    Icons.Outlined.Star
                                },
                                contentDescription = stringResource(R.string.cd_star_rating, star)
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = viewModel::updateNotes,
                label = { Text(stringResource(R.string.notes)) },
                placeholder = {
                    Text(stringResource(R.string.notes_placeholder))
                },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (viewModel.isInputValid()) {
                        viewModel.save()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.validation_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )

                Text(
                    text = if (viewModel.isEditMode) {
                        stringResource(R.string.save_changes)
                    } else {
                        stringResource(R.string.add_to_log)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text(context.getString(R.string.delete_coffee_title, name))
            },
            text = {
                Text(stringResource(R.string.delete_coffee_body))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteCoffee()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(R.string.keep_it))
                }
            }
        )
    }
}