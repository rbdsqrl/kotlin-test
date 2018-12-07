package com.zopsmart.platformapplication

import java.io.Serializable

enum class Widget(private val widgetCode: String) {

    ImageSlideShow("ImageSlideShow"),
    ImageWithText("ImageWithText"),
    LinkCollection("LinkCollection"),
    ProductCollection("ProductCollection"),
    ProductDetail("ProductDetail"),
    ProductScroller("ProductScroller"),
    Order("Order"),
    OrderDetail("OrderDetail"),
    EmptyFragment("EmptyFragment"),
    StoreNotFound("StoreNotFound"),
    Checkout("Checkout"),
    BannerWithText("BannerWithText"),
    BannerWithButton("BannerWithButton"),
    BannerWithMultipleButtons("BannerWithMultipleButtons"),
    CategoryCollection("CategoryCollection"),
    BrandCollection("BrandCollection"),
    Image("Image"),
    ContentBlock("ContentBlock");

    fun getCode(): String {
        return widgetCode
    }

    companion object {
        fun isWidgetSupported(widgetName: String): Boolean {
            val supportedWidgets = mutableListOf<String>()
            for(widget in Widget.values()) {
                supportedWidgets.add(widget.getCode())
            }
            return supportedWidgets.contains(widgetName)
        }
    }
}

enum class CollectionType(var code: String) {
    GRID("GRID"),
    SCROLLER("SCROLLER")
}
enum class ItemType {
    PRODUCT, VARIANT
}

enum class Status constructor(var code: String) {
    PENDING("PENDING"),
    DISPATCHED("DISPATCHED"),
    COMPLETED("COMPLETED"),
    INSTORE("INSTORE"),
    CANCELLED("CANCELLED");
}

enum class PaymentStatus constructor(var code: String) {
    PENDING("PENDING"),
    PAID("PAID"),
    REFUND("REFUND");
}

enum class PaymentType (var code: String) : Serializable {
    COD("COD"),
    ONLINE("ONLINE");
}

enum class TransactionStatus (var code: String) {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");
}

enum class DeliveryType(var code: String) : Serializable {
    PICKUP("PICKUP"),
    DELIVERY("DELIVERY")
}

