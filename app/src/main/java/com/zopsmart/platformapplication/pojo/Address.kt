package com.zopsmart.platformapplication.pojo

import java.io.Serializable

class Address private constructor(var id: Long?, var address: String?, var landmark: String?, var pincode: Long?, var city: String?, var latitude: String?, var longitude: String?) : Serializable {

    constructor(builder: Builder) : this(builder.id, builder.address, builder.landmark, builder.pincode, builder.city, builder.latitude, builder.longitude)

    override fun toString(): String {
        return (address + "\n"
                + landmark + "\n"
                + city + "\n"
                + pincode)
    }

    class Builder {
        internal var id: Long? = 0
        internal var address: String? = null
        internal var landmark: String? = null
        internal var pincode: Long? = 0
        internal var city: String? = null
        internal var latitude: String? = null
        internal var longitude: String? = null

        fun setId(id: Long?): Builder {
            this.id = id
            return this
        }

        fun setAddress(address: String?): Builder {
            this.address = address
            return this
        }

        fun setLandmark(landmark: String?): Builder {
            this.landmark = landmark
            return this
        }

        fun setPincode(pincode: Long?): Builder {
            this.pincode = pincode
            return this
        }

        fun setCity(city: String?): Builder {
            this.city = city
            return this
        }

        fun setLatitude(latitude: String?): Builder {
            this.latitude = latitude
            return this
        }

        fun setLongitude(longitude: String?): Builder {
            this.longitude = longitude
            return this
        }

        fun build(): Address {
            return Address(this)
        }
    }
}
