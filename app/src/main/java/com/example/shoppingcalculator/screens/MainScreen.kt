package com.example.shoppingcalculator.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.components.SaveListDialog
import com.example.shoppingcalculator.components.ShoppingItemCard
import com.example.shoppingcalculator.components.TopBar
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel

@Composable
fun MainScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel
) {
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val maxPriceLength = 6
    val maxItemNameLength = 15
    Scaffold(
        topBar = {
            TopBar(
                title = viewModel.currentList.value.name,
                showBackButton = true,
                onBackClicked = {
                    onNavigate("home")
                },
                onSaveClicked = {
                    if (!(viewModel.currentList.value.items.isEmpty())) {
                        viewModel.saveListToFile()
                        viewModel.loadSavedLists()
                    }
                },
                onNavigate = onNavigate,
                viewModel = viewModel
            )
        }
    ) { padding ->
        Column {
            if (viewModel.sld.value){
                SaveListDialog(
                    {viewModel.sld.value = false},
                    {
                        if (viewModel.currentList.value.items.isNotEmpty()) {
                            viewModel.saveListToFile()
                            viewModel.loadSavedLists()
                            onNavigate("home")
                        }
                    },
                    viewModel
                )
            }else{
                // Items List
            LazyColumn(
                    state = viewModel.listState,
                    modifier = Modifier.weight(1f), contentPadding = padding
                ) {
                    items(viewModel.currentList.value.items) { item ->
                        ShoppingItemCard(
                            viewModel = viewModel,
                            item = item,
                            onDelete = { viewModel.deleteItem(item) },
                            onCheckedChange = { viewModel.toggleItemChecked(item) }
                        )
                    }
                }
                // Total
                Text(
                    text = "${stringResource(R.string.unpaid_bal)}: ${viewModel.appSettings.value[stringResource(R.string.price_sym)]}${viewModel.calculateTotal()}",
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                // Input Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                        .padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {
                            if (it.length <= maxItemNameLength) {
                                itemName = it
                            }
                        },
                        label = { Text(stringResource(R.string.item_name)) },
                        modifier = modifier.weight(0.4f),
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = modifier.width(8.dp))
                    OutlinedTextField(
                        value = itemPrice,
                        onValueChange = {
                            if ( (it.length <= maxPriceLength) && Regex("^\\d*\\.?\\d*$").matches(it) ) itemPrice = it },
                        label = { Text(stringResource(R.string.price)) },
                        modifier = modifier.weight(0.3f),
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(modifier = modifier.width(8.dp))
                    // Quantity controls
                    IconButton(
                        onClick = { quantity++ },
                        modifier = modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = stringResource(R.string.incre))
                    }
                    Text(
                        text = quantity.toString(),
                        modifier = modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = stringResource(R.string.decre))
                    }
                    Button(
                        onClick = {
                            if (itemName.isNotBlank() && itemPrice.isNotBlank()) {
                                viewModel.addItem(
                                    itemName,
                                    itemPrice.toFloat(),
                                    quantity
                                )
                                itemName = ""
                                itemPrice = ""
                                quantity = 1
                                keyboardController?.hide()
                                if (viewModel.appSettings.value[viewModel.context.getString(R.string.keyboard)] as Boolean) focusManager.clearFocus()
                            }
                        },
                        modifier = modifier.width(75.dp)
                    ) {
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    }
}
