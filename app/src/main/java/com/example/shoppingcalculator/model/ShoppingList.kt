package com.example.shoppingcalculator.model

data class ShoppingList(
    val id: Int,
    var name: String,
    var items: MutableList<ShoppingItem> = mutableListOf(),
    var lastModified: Long = System.currentTimeMillis()
)
