package com.example.shoppingcalculator.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.model.ShoppingItem
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel
import java.text.NumberFormat
import java.util.Locale


@Composable
fun ShoppingItemCard(
    viewModel: ShoppingViewModel,
    item: ShoppingItem,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ){
        ListItem(
            headlineContent ={
                Column(
                    horizontalAlignment = Alignment.Start
                ){
                    // Price
                    Text(
                        text = "${stringResource(R.string.price)}: ${viewModel.appSettings.value[stringResource(R.string.price_sym)]}${"%.2f".format(item.price)}",
                        textAlign = TextAlign.Left,
                        fontSize = 16.sp,
                        maxLines = 1,
                        )
                    // Quantity
                    Text(
                        text = "${stringResource(R.string.qty)}: ${item.quantity}",
                        textAlign = TextAlign.Left,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            },
            overlineContent = {
                // ItemName
                Text(
                    text = "${stringResource(R.string.item)}: ${item.name}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
            },
            supportingContent = {
                // Total
                Text(
                    text = "${stringResource(R.string.total)}: ${viewModel.appSettings.value[stringResource(R.string.price_sym)]}${NumberFormat.getNumberInstance(Locale.US).format(item.quantity * item.price)}",
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                )
            },
            leadingContent = {
                IconButton (
                    onClick = onDelete,
                ) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            },
            trailingContent = {
                // Checkbox
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = onCheckedChange,
                )
            }
        )
    }
}











