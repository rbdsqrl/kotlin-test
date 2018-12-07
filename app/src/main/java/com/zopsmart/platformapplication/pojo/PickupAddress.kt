package com.zopsmart.platformapplication.pojo


import java.io.Serializable

class PickupAddress : Serializable {

    var id: Long? = null
    var address: String? = null
    var name: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var clientStoreId: Long? = null

    constructor(id: Long?, address: String?, name: String?, latitude: String?, longitude: String?, clientStoreId: Long?) {
        this.id = id
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.name = name
        this.clientStoreId = clientStoreId
    }

    constructor() {
        //Default empty constructor.
    }


    class Builder {
        private var id: Long? = null
        private var address: String? = null
        private var name: String? = null
        private var latitude: String? = null
        private var longitude: String? = null
        private var clientStoreId: Long? = null

        fun setId(id: Long?): Builder {
            this.id = id
            return this
        }

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setAddress(address: String): Builder {
            this.address = address
            return this
        }

        fun setLatitude(latitude: String): Builder {
            this.latitude = latitude
            return this
        }

        fun setLongitude(longitude: String): Builder {
            this.longitude = longitude
            return this
        }

        fun setClientStoreId(clientStoreId: Long?): Builder {
            this.clientStoreId = clientStoreId
            return this
        }

        fun build(): PickupAddress {
            return PickupAddress(this.id, this.address, this.name, this.latitude, this.longitude, this.clientStoreId)
        }
    }
}
