package com.example.shoppingcalculator.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.components.TopBar
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel


@Composable
fun SettingsScreen(
    onBackPressed: ()->Unit,
    onNavigate: (String)->Unit,
    viewModel: ShoppingViewModel,
    modifier: Modifier= Modifier){
    Scaffold(
        topBar = @Composable{
            TopBar(
                title = stringResource(R.string.settings),
                showBackButton = true,
                onBackClicked = onBackPressed,
                onSaveClicked = {},
                onNavigate = onNavigate,
                viewModel = viewModel
            )
        }
    ){ padding ->
        var isExpanded by remember { mutableStateOf(false) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.padding(padding)
        ) {
            viewModel.appSettings.value.forEach { item->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier.padding(horizontal=4.dp).fillMaxWidth()
                ){
                    Text(item.key)
                    when(item.value){
                        is Boolean -> Switch(
                            checked = item.value as Boolean,
                            onCheckedChange = { newValue ->
                                viewModel.updateSettings( item.key, newValue )
                            }
                        )
                        is String -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                IconButton(onClick = { isExpanded = true }) {
                                    Text(
                                        item.value.toString(),
                                        modifier = modifier.padding(end = 20.dp)
                                    )

                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = stringResource(R.string.drop_down)
                                    )
                                }
                                DropdownMenu(
                                    expanded = isExpanded,
                                    onDismissRequest = { isExpanded = false }
                                ) {
                                    viewModel.priceSym.forEach { sym ->
                                        DropdownMenuItem(
                                            text = { Text(sym) },
                                            onClick = {
                                                isExpanded = false
                                                viewModel.updateSettings( item.key, sym )
                                            }
                                        )
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
                Divider()
            }
            TextButton(
                onClick = { onNavigate("about") },
                modifier = modifier
            ) {
                Text(stringResource(R.string.about))
            }
        }
    }
}