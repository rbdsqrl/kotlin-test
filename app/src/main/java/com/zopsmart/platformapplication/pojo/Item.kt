package com.zopsmart.platformapplication.pojo

import com.zopsmart.platformapplication.ItemType
import org.json.JSONObject

data class Item(
    val id: Long,
    val itemId: Long,
    val selectedVariantId: Long,
    val name: String,
    val fullName: String,
    val hasVariants: Boolean,
    val item_type: ItemType,
    val images: MutableList<String>,
    val url: String?,
    val mrp: Double,
    val discount: Double,
    val sellingPrice: Double,
    val quantity: Int,
    var variants: MutableList<Item>,
    val productId: Int,
    val stock: Long,
    val description: String?,
    var brand: String?,
    var categorySlug: String?,
    val data: JSONObject,
    var parentData: JSONObject,
    val shouldShowProduct: Boolean
) {
    var parentProduct: Item? = null
}