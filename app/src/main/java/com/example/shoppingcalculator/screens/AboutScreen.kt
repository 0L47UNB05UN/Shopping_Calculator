package com.example.shoppingcalculator.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.components.TopBar
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel

@Composable
fun AboutScreen(onBackClicked: () -> Unit, onNavigate: (String)->Unit, viewModel: ShoppingViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.about),
                showBackButton = true,
                onBackClicked = onBackClicked,
                onSaveClicked = {},
                onNavigate=onNavigate,
                viewModel = viewModel
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()).padding(horizontal=12.dp)
        ) {
            Text(
                text = stringResource(R.string.about_app),
                modifier = modifier.padding(bottom = 12.dp)
            )
            Text(
                text = stringResource(R.string.features),
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(bottom = 8.dp)
            )
            FeaturedItem(Icons.Filled.Add, stringResource(R.string.create_unlimited))
            FeaturedItem(Icons.Filled.Star, stringResource(R.string.save_list_for))
            FeaturedItem(Icons.Filled.Share, stringResource(R.string.share_list))
            FeaturedItem(Icons.Filled.Edit, stringResource(R.string.rename_delete_list))
        }
    }
}

@Composable
fun FeaturedItem(icon: ImageVector, info: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical=4.dp)
    ) {
        Icon(icon, contentDescription = info)
        Spacer(modifier = Modifier.padding(horizontal=4.dp))
        Text(text = info)
    }
}