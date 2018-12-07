package com.zopsmart.platformapplication.extension

import com.zopsmart.platformapplication.CATEGORY_PREFIX
import com.zopsmart.platformapplication.ItemType
import com.zopsmart.platformapplication.PaymentStatus
import com.zopsmart.platformapplication.Status
import com.zopsmart.platformapplication.pojo.*
import com.zopsmart.platformapplication.repository.CartItemRepository
import com.zopsmart.platformapplication.repository.CustomerRepository
import com.zopsmart.platformapplication.repository.Repository
import org.json.JSONArray
import org.json.JSONObject

//keys for parsing
private const val IMAGE = "image"
private const val IMAGES = "images"
private const val IMAGE_URL = "imageUrl"
private const val BRAND = "brand"
private const val LINK = "link"
private const val DESCRIPTION = "description"
private const val FULL_NAME = "fullName"
private const val CATEGORIES = "categories"
private const val SLUG = "slug"
private const val ITEM = "item"
private const val ENTITY_TYPE = "entityType"
private const val VARIANT_ENTITY = "VARIANT"
private const val PRODUCT_ENTITY = "PRODUCT"
private const val PRODUCT_ID = "productId"
private const val MRP = "mrp"
private const val DISCOUNT = "discount"
private const val STOCK = "stock"
private const val HAS_VARIANTS = "hasVariants"
private const val VARIANTS = "variants"
private const val VARIANT = "variant"
private const val STORE_SPECIFIC_DATA = "storeSpecificData"
private const val REFERENCE_NUMBER = "referenceNumber"
private const val STATUS = "status"
private const val PAYMENT_STATUS = "paymentStatus"
private const val AMOUNT = "amount"
private const val PENDING_AMOUNT = "pendingAmount"
private const val SHIPPING = "shipping"
private const val COUPON_DISCOUNT = "couponDiscount"
private const val INVOICE_AMOUNT = "invoiceAmount"
private const val PLACED_ON = "placedOn"
private const val CREATED_AT = "createdAt"
private const val COMPLETED_AT = "completedAt"
private const val ITEMS = "items"
private const val CUSTOMER = "customer"
private const val ORDER_DETAILS = "orderDetails"
private const val ORDER_ITEM_ID = "orderItemId"
private const val ORDERED_QTY = "orderedQuantity"
private const val DELIVERED_QTY = "deliveredQuantity"

internal const val ORDER = "order"


//Extensions to extract JSONObjects from data objects
fun List<CartItem>?.toParams(time: Long): JSONObject {
    val params = JSONObject()
    if (this != null) {
        val cart = JSONArray()
        for (cartItem in this) {
            val jsonObject = JSONObject()
            jsonObject.put(Repository.ID, cartItem.itemId)
            jsonObject.put(CartItemRepository.keyQ, "${cartItem.quantity}")
            cart.put(jsonObject)
        }
        params.put(CartItemRepository.keyCart, cart)
        params.put(CartItemRepository.keyTime, time)
    }
    return params
}

//Extensions to extract data objects from JSONs
fun JSONObject.getCoupon(): Coupon {
    val data = this.jObject(Repository.DATA)
    val discountObject = data.jObject(CartItemRepository.keyDiscount)
    val discountAmount = discountObject.extract(CartItemRepository.keyDiscount, 0.0)
    val coupon = data.jObject(CartItemRepository.keyCoupon)
    val couponCode = coupon.extract(CartItemRepository.keyCouponCode, "No coupon")
    return Coupon(couponCode, discountAmount)
}

fun JSONObject.getAddress(): Address {
    val data = jObject(Repository.DATA)
    val addressJSON = data.jObject(CustomerRepository.ADDRESS)
    val id = addressJSON.extract(Repository.ID, 0L)
    val landmark = addressJSON.extract(CustomerRepository.LANDMARK, null as String?)
    val address = addressJSON.extract(CustomerRepository.ADDRESS, null as String?)
    val pincode = addressJSON.extract(CustomerRepository.PINCODE, 0L)
    val city = addressJSON.extract(CustomerRepository.CITY, null as String?)
    val longitude = addressJSON.extract(CustomerRepository.LONGITUDE, null as String?)
    val latitude = addressJSON.extract(CustomerRepository.LATITUDE, null as String?)

    return Address.Builder()
        .setId(id)
        .setAddress(address)
        .setCity(city)
        .setPincode(pincode)
        .setLandmark(landmark)
        .setLatitude(latitude)
        .setLongitude(longitude)
        .build()
}

fun JSONArray.getAddressList(): List<Address> {
    val addressList = mutableListOf<Address>()
    for (i in 0..this.length()) {
        val obj = this.getJSONObject(i)
        addressList.add(obj.getAddress())
    }
    return addressList
}

fun JSONObject.getCarousals(): List<Carousel> {
    val carouselData = jArray(IMAGES)
    val carouselList = mutableListOf<Carousel>()
    for (i in 0..carouselData.length()) {
        carouselList.add(carouselData.getJSONObject(i).getCarousel())
    }
    return carouselList
}

fun JSONObject.getCarousel(): Carousel {
    val url = extract(LINK, "")
    val imageUrl = extract(IMAGE_URL, "")
    return Carousel(imageUrl!!, url!!)
}

fun JSONObject.getImageWithText(): ImageWithText {
    val link = extract(LINK, "")
    val imageURl = extract(IMAGE_URL, "")
    val title = extract(Repository.TITLE, "")
    val description = extract(DESCRIPTION, "")
    return ImageWithText(imageURl!!, link!!, title!!, description!!)
}

fun JSONObject.getItems(): List<Item> {
    val itemList = mutableListOf<Item>()
    try {
        val collection = jObject(Repository.COLLECTION)
        val products = collection.jArray(Repository.PRODUCT)
        for (i in 0..products.length()) {
            val item = products.getJSONObject(i).getItem()
            if (!item.shouldShowProduct) {
                if (item.hasVariants && item.variants.size != 0) itemList.add(item)
            } else {
                itemList.add(item)
            }
        }
        return itemList
    } catch (e: Exception) {

    }
    return itemList
}

fun JSONObject.getItem(): Item {
    val name = extract(Repository.NAME, "")
    val fullName = extract(FULL_NAME, name)
    val description = extract(DESCRIPTION, null as String?)

    val catArray = jArray(CATEGORIES)
    var categorySlug: String? = null
    if (catArray.length() != 0) {
        categorySlug = catArray.getJSONObject(0).extract(SLUG, null as String?)
        if (categorySlug != null && categorySlug.startsWith(CATEGORY_PREFIX))
            categorySlug = categorySlug.replaceFirst(CATEGORY_PREFIX, "")
    }
    val id = extract(Repository.ID, 0L)

    val itemJSON = jObject(ITEM)
    val itemId = itemJSON.extract(Repository.ID, 0L)
    var itemType = ItemType.PRODUCT
    if (itemJSON.extract(ENTITY_TYPE, PRODUCT_ENTITY).equals(VARIANT_ENTITY)) {
        itemType = ItemType.VARIANT
    }
    val productId = extract(PRODUCT_ID, 0)
    val ssdArray = jArray(STORE_SPECIFIC_DATA) //Store specific data array
    val shouldShowProduct = ssdArray.length() != 0
    val ssData = ssdArray.getJSONObject(0) //TODO change to selected store (Store specific data)
    val mrp = ssData.extract(MRP, 0.0)
    val discount = ssData.extract(DISCOUNT, 0.0)
    //TODO (val sellingPrice = ssData.extract(SELLING_PRICE, 0.0))
    val sellingPrice = mrp!! - discount!!
    val stockKey = ssData.get(STOCK)

    val stock: Long? = if (stockKey::class == Boolean::class) {
        val stockVal = ssData.extract(STOCK, true)
        if (stockVal!!) Long.MAX_VALUE else 0L
    } else {
        ssData.extract(STOCK, 0L)
    }

    val url = extract(SLUG, null as String?)

    val list = mutableListOf<String>()
    val jsonImages = jArray(IMAGES)
    for (i in 0..jsonImages.length()) {
        list.add(jsonImages.getString(i))
    }

    val brandObj = jObject(BRAND)
    val brand = brandObj.extract(Repository.NAME, null as String?)

    val hasVariants = extract(HAS_VARIANTS, 0) == 1
    val variants = mutableListOf<Item>()
    if (!hasVariants) {
        val mainItem = Item(
            id!!,
            itemId!!,
            0,
            name!!,
            fullName!!,
            false,
            itemType,
            list,
            url,
            mrp,
            discount,
            sellingPrice,
            0,
            mutableListOf(),
            productId!!,
            stock!!,
            description,
            brand,
            categorySlug,
            this,
            this,
            shouldShowProduct
        )
        variants.add(mainItem)
    } else {
        variants.addAll(getVariantItems(jArray(VARIANTS), brand, this, categorySlug))
    }
    val selectedVariant = variants[0] //TODO change later to read variants from response if it exists
    val selectedVariantId = selectedVariant.id

    val item = Item(
        id!!,
        itemId!!,
        selectedVariantId,
        name!!,
        fullName!!,
        hasVariants,
        itemType,
        list,
        url,
        mrp,
        discount,
        sellingPrice,
        0,
        variants,
        productId!!,
        stock!!,
        description,
        brand,
        categorySlug,
        this,
        this,
        shouldShowProduct
    )
    //TODO change implementation of setting parent Product
    if (item.variants.size > 0) {
        val itemVariants = mutableListOf<Item>()
        for (variant in item.variants) {
            variant.parentProduct = item
            itemVariants.add(variant)
        }
        item.variants = itemVariants
    }
    return item
}

private fun getVariantItems(
    jArray: JSONArray,
    brand: String?,
    parentData: JSONObject,
    categorySlug: String?
): MutableList<Item> {
    val variants = mutableListOf<Item>()
    for (i in 0..jArray.length()) {
        val jsonObject = jArray.getJSONObject(i)
        val variant = jsonObject.getItem()
        variant.brand = brand
        variant.categorySlug = categorySlug
        variant.parentData = parentData
        variants.add(variant)
    }
    return variants
}

fun JSONObject.isOrderDetail(): Boolean {
    val order = get(ORDER)
    return (order::class == JSONObject::class)
}

fun JSONObject.createOrder(): Order {
    val refNo = extract(REFERENCE_NUMBER, null as String?)
    val statusStr = extract(STATUS, Status.PENDING.code)
    var orderStatus: Status = Status.PENDING
    for (status in Status.values()) {
        if (status.code.equals(statusStr, true)) {
            orderStatus = status
            break
        }
    }

    val paymentStatusStr = extract(PAYMENT_STATUS, null as String?)
    val paymentStatus = if (paymentStatusStr == null) PaymentStatus.PENDING else PaymentStatus.valueOf(paymentStatusStr)
    val amount = extract(AMOUNT, 0.0)
    val pendingAmount = extract(PENDING_AMOUNT, 0.0)
    val shippingCharges = extract(SHIPPING, 0.0)
    val discount = extract(DISCOUNT, 0.0)
    val couponDiscount = extract(COUPON_DISCOUNT, 0.0)
    val invoiceAmount = extract(INVOICE_AMOUNT, 0.0)
    val amountPaid = (amount!! + shippingCharges!! - discount!!) - pendingAmount!!
    val placedOn = extract(PLACED_ON, null as String?)
    val createdAt = extract(CREATED_AT, null as String?)
    val completedAt = extract(COMPLETED_AT, null as String?)

    val itemJArray = jArray(ITEMS)
    val nITmes = itemJArray.length()

    val orderItems = mutableListOf<OrderItem>()
    val customerObj = jObject(CUSTOMER)

    val name = customerObj.extract(Repository.NAME, null as String?)
    for (i in 0..itemJArray.length()) {
        val item = itemJArray.getJSONObject(i)
        val itemId = extract(Repository.ID, 0L)
        val entityType = extract(ENTITY_TYPE, PRODUCT_ENTITY)
        val itemName = if (entityType!!.equals(PRODUCT_ENTITY, true)) {
            val product = item.jObject(Repository.PRODUCT)
            product.extract(Repository.NAME, null as String?)
        } else {
            val product = item.jObject(VARIANT)
            product.extract(FULL_NAME, null as String?)
        }
        val orderDetails = item.jObject(ORDER_DETAILS)
        val orderItemId = orderDetails.extract(ORDER_ITEM_ID, 0L)
        val orderedQty = orderDetails.extract(ORDERED_QTY, 0.0)
        val deliveredQty = orderDetails.extract(DELIVERED_QTY, 0.0)
        val mrp = orderDetails.extract(MRP, 0.0)
        val discount = orderDetails.extract(DISCOUNT, 0.0)

        val orderItem = OrderItem(itemId!!, orderItemId!!, itemName, mrp, discount, orderedQty, deliveredQty)
        orderItems.add(orderItem)
    }


}



