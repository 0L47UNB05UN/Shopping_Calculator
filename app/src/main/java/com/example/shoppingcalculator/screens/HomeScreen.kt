package com.example.shoppingcalculator.screens

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.components.RenameFileDialog
import com.example.shoppingcalculator.components.SaveListDialog
import com.example.shoppingcalculator.components.TopBar
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel


@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel,
) {
    viewModel.loadSavedLists()
    val activity = LocalActivity.current
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.home),
                showBackButton = false,
                onBackClicked = {},
                onSaveClicked = {},
                viewModel = viewModel,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.sld.value = true
                    viewModel.currentList.value.items = mutableListOf()
                          }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { padding ->
        if (viewModel.sld.value){
            SaveListDialog({viewModel.sld.value = false}, { onNavigate("main") }, viewModel)
        }
        else{
            LazyColumn(Modifier, contentPadding = padding) {
                if (viewModel.savedLists.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.no_list),
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(viewModel.savedLists) { list ->
                        if (viewModel.rfd.value){
                            RenameFileDialog(
                                fileName = list,
                                dismissDialog ={viewModel.rfd.value = false},
                                onConfirmDialog = {viewModel.loadSavedLists()},
                                viewModel = viewModel
                            )
                        }
                        else{
                            var isExpanded by remember { mutableStateOf(false) }
                            Card(
                                modifier = Modifier
                                    .clickable { if (viewModel.loadListFromFile(list)) onNavigate("main") }
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.ShoppingCart,
                                        contentDescription=stringResource(R.string.shopping_cart),
                                        modifier = Modifier.padding(horizontal=8.dp)
                                        )
                                    Text(
                                        list,
                                        modifier = Modifier
                                            .padding(16.dp).weight(1f),
                                        textAlign = TextAlign.Left,
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.End
                                    ){
                                        IconButton( onClick = { isExpanded = true } ) {
                                            Icon(
                                                Icons.Filled.MoreVert,
                                                contentDescription = stringResource(R.string.more)
                                            )
                                        }
                                        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.rename)) },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Filled.Create,
                                                        contentDescription = stringResource(R.string.rename)
                                                    )
                                                },
                                                onClick = {
                                                    isExpanded = false
                                                    viewModel.rfd.value = true
                                                }
                                            )
                                            Divider()
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.share)) },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Filled.Share,
                                                        contentDescription = stringResource(R.string.share)
                                                    )
                                                },
                                                onClick = {
                                                    isExpanded = false
                                                    val shareIntent = viewModel.createShareIntent()
                                                    if (shareIntent != null) {
                                                        activity?.startActivity(
                                                            Intent.createChooser(
                                                                shareIntent,
                                                                viewModel.context.getString(R.string.share_detail)
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                            Divider()
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.delete)) },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Filled.Delete,
                                                        contentDescription = stringResource(R.string.delete)
                                                    )
                                                },
                                                onClick = {
                                                    isExpanded = false
                                                    viewModel.deleteFile(list)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
