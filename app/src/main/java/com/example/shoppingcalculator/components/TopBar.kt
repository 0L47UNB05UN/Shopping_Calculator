package com.example.shoppingcalculator.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    showBackButton: Boolean,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: ShoppingViewModel,
) {
    TopAppBar(
        title = @Composable {  Text(title) },
        navigationIcon =
                @Composable {
                    if (showBackButton)
                        IconButton(onClick = onBackClicked) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
        actions = {
            if ( title != stringResource(R.string.settings) ){
                if (title == stringResource(R.string.home) ) {
                    IconButton(onClick = { onNavigate("settings") }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                } else {
                    var isExpanded by remember {
                        mutableStateOf(false)
                    }
                    IconButton(onClick = { isExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.drop_down)
                        )
                    }
                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                    ) {
                        if (title == viewModel.currentList.value.name ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.save)) },
                                onClick = {
                                    isExpanded = false
                                    onSaveClicked()
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.save_as)) },
                                onClick = {
                                    isExpanded = false
                                    viewModel.sld.value = true
                                    viewModel.currentList.value = viewModel.currentList.value.copy()
                                }
                            )
                            Divider()
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings)) },
                            onClick = {
                                isExpanded = false
                                onNavigate("settings")
                            },
                        )
                    }
                }
            }
        }
    )
}
