package com.example.shoppingcalculator.model

data class ShoppingItem(
    val id: Int,
    var name: String,
    var price: Float,
    var quantity: Int,
    var isChecked: Boolean = false
)
