package com.dresta0056.whowantcoffee.ui.detail

import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import com.dresta0056.whowantcoffee.ui.theme.WhoWantCoffeeTheme
import com.dresta0056.whowantcoffee.R
import com.dresta0056.whowantcoffee.util.getProcessDisplayNameRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    val snackbarMessage = snackbarMessageRes?.let { resId ->
        snackbarMessageArg?.let { arg ->
            stringResource(resId, arg)
        } ?: stringResource(resId)
    }
    val undoLabel = stringResource(R.string.undo)
    val validationToast = stringResource(R.string.validation_toast)
    val shareTitle = stringResource(R.string.share_coffee)
    val processDisplayName = getProcessDisplayNameRes(process)?.let { stringResource(it) } ?: process

    LaunchedEffect(isFinished) {
        if (isFinished) {
            navController.popBackStack()

            snackbarScope.launch {
                snackbarMessage?.let { message ->
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = if (snackbarMessageRes == R.string.snackbar_archived) {
                            undoLabel
                        } else {
                            null
                        },
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoArchive()
                    }
                }
            }
        }
    }

    CoffeeDetailContent(
        name = name,
        process = process,
        rating = rating,
        notes = notes,
        isEditMode = viewModel.isEditMode,
        onBackClick = { navController.popBackStack() },
        onUpdateName = viewModel::updateName,
        onUpdateProcess = viewModel::updateProcess,
        onUpdateRating = viewModel::updateRating,
        onUpdateNotes = viewModel::updateNotes,
        onSave = {
            if (viewModel.isInputValid()) {
                viewModel.save()
            } else {
                Toast.makeText(
                    context,
                    validationToast,
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        onShareClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    viewModel.shareText(processDisplayName)
                )
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    shareTitle
                )
            )
        },
        onArchiveClick = viewModel::archiveCoffee,
        onDeleteConfirm = viewModel::deleteCoffee
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoffeeDetailContent(
    name: String,
    process: String,
    rating: Int,
    notes: String,
    isEditMode: Boolean,
    onBackClick: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateProcess: (String) -> Unit,
    onUpdateRating: (Int) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onSave: () -> Unit,
    onShareClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var processExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var nameTouched by remember { mutableStateOf(false) }
    var processTouched by remember { mutableStateOf(false) }
    var wasSaveAttempted by remember { mutableStateOf(false) }

    val nameError = (wasSaveAttempted || nameTouched) && name.isBlank()
    val processError = (wasSaveAttempted || processTouched) && process.isBlank()

    val processes = listOf("Washed", "Honey", "Natural")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) {
                            stringResource(R.string.edit_coffee_title)
                        } else {
                            stringResource(R.string.add_coffee_title)
                        },
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
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = onShareClick) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.cd_share_coffee)
                            )
                        }

                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.cd_menu)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
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
                                    onArchiveClick()
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
                onValueChange = {
                    onUpdateName(it)
                    nameTouched = true
                },
                label = { Text(stringResource(R.string.coffee_name)) },
                isError = nameError,
                supportingText = {
                    if (nameError) {
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
                    value = getProcessDisplayNameRes(process)?.let { stringResource(it) } ?: process,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.process)) },
                    isError = processError,
                    supportingText = {
                        if (processError) {
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
                            text = { Text(getProcessDisplayNameRes(item)?.let { stringResource(it) } ?: item) },
                            onClick = {
                                onUpdateProcess(item)
                                processTouched = true
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Row {
                    for (star in 1..5) {
                        val isSelected = star <= rating
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()

                        val starTint by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant,
                            label = "starTint"
                        )
                        val starScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.9f else if (isSelected) 1.1f else 1f,
                            label = "starScale"
                        )

                        IconButton(
                            onClick = { onUpdateRating(star) },
                            interactionSource = interactionSource
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.Star,
                                contentDescription = stringResource(R.string.cd_star_rating, star),
                                tint = starTint,
                                modifier = Modifier.scale(starScale)
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = onUpdateNotes,
                label = { Text(stringResource(R.string.notes)) },
                placeholder = {
                    Text(stringResource(R.string.notes_placeholder))
                },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    wasSaveAttempted = true
                    onSave()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )

                Text(
                    text = if (isEditMode) {
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
                Text(
                    text = stringResource(R.string.delete_coffee_title, name),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(stringResource(R.string.delete_coffee_body))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirm()
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

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun CoffeeDetailEditPreview() {
    WhoWantCoffeeTheme {
        CoffeeDetailContent(
            name = "Ethiopia Yirgacheffe",
            process = "Washed",
            rating = 5,
            notes = "Floral, Citrus, Bergamot",
            isEditMode = true,
            onBackClick = {},
            onUpdateName = {},
            onUpdateProcess = {},
            onUpdateRating = {},
            onUpdateNotes = {},
            onSave = {},
            onShareClick = {},
            onArchiveClick = {},
            onDeleteConfirm = {}
        )
    }
}
