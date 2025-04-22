package com.example.shoppingcalculator.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameFileDialog(
    fileName: String,
    dismissDialog: () -> Unit,
    onConfirmDialog: () -> Unit,
    viewModel: ShoppingViewModel,
    modifier: Modifier = Modifier,
){
    AlertDialog(
        onDismissRequest = dismissDialog,
        properties = DialogProperties(dismissOnBackPress = true, decorFitsSystemWindows = true)
    ){
        var text = remember{
            mutableStateOf("${viewModel.context.getString(R.string.new_list)}${viewModel.savedLists.size+1}")
        }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical=4.dp)
        ) {
            OutlinedTextField(
                value = text.value,
                onValueChange = {text.value = it},
                singleLine = true,
                label = { Text(stringResource(R.string.name_of_list_file)) },
                placeholder = { Text(viewModel.currentList.value.name + viewModel.currentList.value.id) }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Button(
                    modifier = modifier.padding(8.dp),
                    onClick = dismissDialog
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    modifier = modifier.padding(8.dp),
                    onClick = {
                        if ( !( text.value.isEmpty()) ) {
                            dismissDialog()
                            viewModel.renameFile(fileName, text.value)
                            onConfirmDialog()
                        }
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    }
}
