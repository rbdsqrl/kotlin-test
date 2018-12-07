package com.zopsmart.platformapplication

import com.zopsmart.platformapplication.exception.ZSException
import com.zopsmart.platformapplication.extension.*
import com.zopsmart.platformapplication.repository.Repository
import com.zopsmart.platformapplication.repository.Repository.Companion.DATA
import com.zopsmart.platformapplication.repository.Repository.Companion.POSITION
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ZSParser {

    companion object {

        private const val LAYOUT_TYPE = "layoutType"
        private const val SCROLLER = "scroller"

        @Throws(Exception::class)
        fun getObjectifiedResponse(response: JSONObject): JSONObject {
            return getObjectifiedResponse(response, false)
        }

        @Throws(Exception::class)
        private fun getObjectifiedResponse(response: JSONObject, ignoreSettings: Boolean): JSONObject {
            try {
                val mainData = response.jObject(DATA)
                val page = mainData.jObject(Repository.PAGE)
                var layouts = page.jArray(Repository.LAYOUTS)

                layouts = getParsedResponse(layouts)
                var position = 0
                for (index in 0 until layouts.length()) {
                    val layout = layouts.getJSONObject(index)
                    val widgetName = getWidgetName(layout)
                    val widgetData = getWidgetData(layout)
                    var layoutTitle = getWidgetTitle(layout)

                    val widget: Widget
                    try {
                        widget = Widget.valueOf(widgetName!!)
                    } catch (e: IllegalArgumentException) {
                        continue
                    }

                    when (widget) {
                        Widget.ImageSlideShow -> {
                            val data = widgetData.getCarousals()
                            layout.put(DATA, data)
                            layout.put(POSITION, position++)
                        }

                        Widget.ImageWithText -> {
                            val data = widgetData.getImageWithText()
                            layout.put(DATA, data)
                            layout.put(POSITION, position++)
                        }

                        Widget.ProductScroller -> {
                            val data = widgetData.getItems()
                            if (data.isNotEmpty()) {
                                val collection = widgetData.jObject(Repository.COLLECTION)
                                val count = collection.extract(Repository.COUNT, 0)
                                val offset = collection.extract(Repository.OFFSET, 0)
                                val limit = collection.extract(Repository.LIMIT, 0)
                                layout.put(Repository.COUNT, count)
                                layout.put(Repository.OFFSET, offset)
                                layout.put(Repository.LIMIT, limit)

                                layout.put(DATA, data)
                                layout.put(POSITION, position++)
                            }
                        }

                        Widget.ProductCollection -> {
                            val data = widgetData.getItems()

                            if (data.isNotEmpty()) {
                                val collection = widgetData.jObject(Repository.COLLECTION)
                                val count = collection.extract(Repository.COUNT, 0)
                                val offset = collection.extract(Repository.OFFSET, 0)
                                val limit = collection.extract(Repository.LIMIT, 0)
                                layout.put(Repository.COUNT, count)
                                layout.put(Repository.OFFSET, offset)
                                layout.put(Repository.LIMIT, limit)

                                layout.put(DATA, data)
                                layout.put(POSITION, position++)
                            }
                        }

                        Widget.ProductDetail -> {
                            val data = widgetData.getItem()
                            layout.put(DATA, data)
                            layout.put(POSITION, position++)
                        }

                        Widget.LinkCollection -> {
                            layout.put(POSITION, position++)
                        }

                        Widget.Order -> {
                            layout.put(POSITION, position++)

                            val collection = widgetData.jObject(Repository.COLLECTION)

                            if (collection.isOrderDetail()) {
                                layoutTitle = Widget.OrderDetail.getCode()
                                val orderJSON = collection.jObject(ORDER)
                                val order = collection.createOrder()
                                layout.put(Repository.NAME, layoutTitle)
                                layout.put(DATA, order)
                            } else {
                                val count = collection.extract(Repository.COUNT, 0)
                                val offset = collection.extract(Repository.OFFSET, 0)
                                val limit = collection.extract(Repository.LIMIT, 0)
                                layout.put(Repository.COUNT, count)
                                layout.put(Repository.OFFSET, offset)
                                layout.put(Repository.LIMIT, limit)
                                val orderList = MOrder.createOrders(collection)
                                layout.put(DATA, orderList)
                            }

                        }

                        Widget.BannerWithText -> {
                            val data = MBannerWithText.getData(widgetData)
                            layout.put(POSITION, position++)
                            layout.put(DATA, data)
                        }

                        Widget.BannerWithButton -> {
                            val data = MBannerWithButton.getData(widgetData)
                            layout.put(POSITION, position++)
                            layout.put(DATA, data)
                        }

                        Widget.BannerWithMultipleButtons -> {
                            val data = MBannerWithMultipleButtons.getData(widgetData)
                            layout.put(POSITION, position++)
                            layout.put(DATA, data)
                        }

                        Widget.CategoryCollection -> {
                            val data = MCategoryCollection.getCategoryCollection(widgetData)
                            if (data.getCategories() == null || data.getCategories().size() === 0) {
                                break
                            }
                            val configData = layout.getJSONObject(DATA)
                            val layoutType = configData.extract(LAYOUT_TYPE, CollectionType.GRID.code)
                            val collectionType =
                                if (layoutType.equals(SCROLLER, ignoreCase = true)) CollectionType.SCROLLER else CollectionType.GRID
                            data.setCollectionType(collectionType)

                            layout.put(POSITION, position++)
                            layout.put(DATA, data)
                        }

                        Widget.BrandCollection -> {
                            val data = MBrandCollection.getBrandCollection(widgetData)
                            if (data.getBrands() != null && data.getBrands().size() != 0) {
                                val configData = layout.getJSONObject(DATA)
                                val layoutType = configData.extract(LAYOUT_TYPE, CollectionType.GRID.code)
                                val collectionType =
                                    if (layoutType.equals(SCROLLER, ignoreCase = true)) CollectionType.SCROLLER else CollectionType.GRID
                                data.setCollectionType(collectionType)

                                layout.put(POSITION, position++)
                                layout.put(DATA, data)
                            }
                        }

                        Widget.Image -> {
                            val data = ImageData.getImageData(widgetData)
                            layout.put(POSITION, position++)
                            layout.put(DATA, data)
                        }

                        Widget.ContentBlock -> {
                            val data = ContentBlockData.getContentBlockData(widgetData)
                            layout.put(POSITION, position++)
                            layout.put(DATA, data)
                        }

                        else -> {
                        }
                    }
                    layout.put(Repository.TITLE, layoutTitle)
                    layouts.put(index, layout)
                }
                //TODO : Set this in the organization call.
                /*if (!ignoreSettings && (response.isNull(APIConstants.ORGANIZATION) || layouts.length() == 0)) {
                    JSONObject emptyObject = new JSONObject();
                    if (response.isNull(APIConstants.ORGANIZATION))
                        emptyObject.put(Repository.NAME, ZSLayout.Widget.StoreNotFound.getCode());
                    else
                        emptyObject.put(Repository.NAME, ZSLayout.Widget.EmptyFragment.getCode());
                    emptyObject.put(POSITION, 0);
                    layouts = new JSONArray();
                    layouts.put(0, emptyObject);
                }*/
                layouts = removeLayoutsWithoutPosition(layouts)
                response.put(Repository.LAYOUTS, layouts)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return response
        }

        /**
         * The parsing stage breaks the loop and does not add a 'position' key for layouts that do not have relevant data.
         * This method removes the layouts that do not have the position key.
         * @param response
         * @return
         */
        fun removeLayoutsWithoutPosition(response: JSONArray): JSONArray {
            val rectifiedResponse = JSONArray()
            for (i in 0 until response.length()) {
                try {
                    val layout = response.getJSONObject(i)
                    if (layout.has("position")) {
                        rectifiedResponse.put(layout)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            return rectifiedResponse
        }

        @Throws(ZSException::class, JSONException::class)
        private fun getPaginationData(widgetData: JSONObject): JSONObject {
            val paginationData = JSONObject()
            val collection = widgetData.jObject(Repository.COLLECTION)
            if (collection.length() == 0) {
                return paginationData
            }

            val count = collection.extract(Repository.COUNT, 0)
            val offset = collection.extract(Repository.OFFSET, 0)
            val limit = collection.extract(Repository.LIMIT, 0)
            paginationData.put(Repository.COUNT, count)
            paginationData.put(Repository.OFFSET, offset)
            paginationData.put(Repository.LIMIT, limit)
            return paginationData
        }

        @Throws(JSONException::class)
        fun getParsedResponse(jsonArray: JSONArray): JSONArray {
            val knownLayouts = removeUnknownLayouts(jsonArray)
            val layoutSize = knownLayouts.length()
            for (i in 0 until layoutSize) {
                try {
                    val widget = knownLayouts.getJSONObject(i)
                    val widgetName = widget.getString(Repository.NAME)
                    val data = widget.getJSONObject(Repository.VALUE)
                    var metadata = JSONObject()
                    if (widget.has(Repository.DATA)) metadata = widget.getJSONObject(Repository.DATA)
                    if (!widgetName.equals(Widget.ProductCollection.getCode(), ignoreCase = true)) {
                        continue
                    }

                    if (i != layoutSize - 1) {
                        val jsonObject = JSONObject()
                        jsonObject.put(Repository.NAME, Widget.ProductScroller.getCode())
                        jsonObject.put(Repository.VALUE, data)
                        jsonObject.put(Repository.METADATA, metadata)
                        knownLayouts.put(i, jsonObject)
                    } else {
                        val jsonObject = JSONObject()
                        jsonObject.put(Repository.NAME, Widget.ProductCollection.getCode())
                        jsonObject.put(Repository.VALUE, data)
                        jsonObject.put(Repository.METADATA, metadata)
                        knownLayouts.put(i, jsonObject)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            return knownLayouts
        }

        @Throws(JSONException::class)
        fun removeUnknownLayouts(jsonArray: JSONArray): JSONArray {//TODO : Remove layouts with size = 0;.
            val knownLayoutsArray = JSONArray()
            for (index in 0 until jsonArray.length()) {
                val layoutObject = jsonArray.getJSONObject(index)
                val widgetName = layoutObject.getString(Repository.NAME)
                if (!Widget.isWidgetSupported(widgetName)) continue
                knownLayoutsArray.put(layoutObject)
            }
            return knownLayoutsArray
        }

        @Throws(ZSException::class, JSONException::class)
        fun getWidgetDataFromResult(result: JSONObject, widget: String): JSONObject? {

            val layouts = result.jArray(Repository.LAYOUTS)

            for (i in 0 until layouts.length()) {
                val layout = layouts.getJSONObject(i)
                val widgetName = getWidgetName(layout)
                val widgetData = getWidgetData(layout)
                if (widgetName!!.equals(widget, ignoreCase = true)) {
                    return widgetData
                }
            }
            return null
        }

        fun getWidgetTitle(layout: JSONObject): String? {
            try {
                val data = layout.getJSONObject(Repository.VALUE)
                if (data.has(Repository.TITLE))
                    return data.getString(Repository.TITLE)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null
        }

        fun getWidgetData(layout: JSONObject): JSONObject {
            try {
                return layout.getJSONObject(Repository.VALUE)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return JSONObject()
        }

        fun getWidgetDataFromData(layout: JSONObject): JSONObject {
            try {
                return layout.getJSONObject(DATA)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return JSONObject()
        }

        fun getWidgetName(layout: JSONObject): String? {
            try {
                return layout.getString(Repository.NAME)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null
        }
    }


}