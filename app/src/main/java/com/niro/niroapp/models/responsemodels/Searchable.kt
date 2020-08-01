package com.niro.niroapp.models.responsemodels

import android.os.Parcel
import android.os.Parcelable

sealed class Searchable {}

data class User(
    val id: String?,
    val fullName: String?,
    val phoneNumber: String?,
    val businessName: String?,
    val selectedCategories: List<CommodityItem>?,
    val selectedMandi: MandiLocation?,
    val userType: String?,
    val ratings: Float?
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.createTypedArrayList(CommodityItem.CREATOR),
        source.readParcelable<MandiLocation>(MandiLocation::class.java.classLoader),
        source.readString(),
        source.readValue(Float::class.java.classLoader) as Float?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(fullName)
        writeString(phoneNumber)
        writeString(businessName)
        writeTypedList(selectedCategories)
        writeParcelable(selectedMandi, 0)
        writeString(userType)
        writeValue(ratings)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}


data class MandiLocation(
    val district: String?,
    val market: String?,
    val state: String?,
    var isSelected: Boolean = false
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(district)
        writeString(market)
        writeString(state)
        writeInt((if (isSelected) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MandiLocation> =
            object : Parcelable.Creator<MandiLocation> {
                override fun createFromParcel(source: Parcel): MandiLocation = MandiLocation(source)
                override fun newArray(size: Int): Array<MandiLocation?> = arrayOfNulls(size)
            }
    }
}


data class Commodity(
    val id: String,
    val image: String,
    val name: String

) : Searchable()


data class CommodityItem(
    val id: String?,
    val image: String?,
    val name: String?,
    val categoryName: String?,
    var isSelected: Boolean = false
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(image)
        writeString(name)
        writeString(categoryName)
        writeInt((if (isSelected) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CommodityItem> =
            object : Parcelable.Creator<CommodityItem> {
                override fun createFromParcel(source: Parcel): CommodityItem = CommodityItem(source)
                override fun newArray(size: Int): Array<CommodityItem?> = arrayOfNulls(size)
            }
    }
}


data class Contact(val name: String?, val number: String?, val email: String?) : Searchable(),
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(number)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}


data class UserContact(
    val contactId: String?,
    val currentUserId: String?,
    val contactName: String?,
    val phoneNumber: String?,
    val userLocation: MandiLocation?,
    val selectedCommodity: List<CommodityItem>?,
    val businessName: String?,
    val avgRatings: String?
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readParcelable<MandiLocation>(MandiLocation::class.java.classLoader),
        source.createTypedArrayList(CommodityItem.CREATOR),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(contactId)
        writeString(currentUserId)
        writeString(contactName)
        writeString(phoneNumber)
        writeParcelable(userLocation, 0)
        writeTypedList(selectedCommodity)
        writeString(businessName)
        writeString(avgRatings)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserContact> = object : Parcelable.Creator<UserContact> {
            override fun createFromParcel(source: Parcel): UserContact = UserContact(source)
            override fun newArray(size: Int): Array<UserContact?> = arrayOfNulls(size)
        }
    }
}


data class UserOrder(
    val orderId: String?,
    val userContact: UserContact?,
    val orderAmount: Double = 0.0,
    val receivingDate: String?,
    val orderCommodity: CommodityItem?,
    val imageName: List<String>?
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readParcelable<UserContact>(UserContact::class.java.classLoader),
        source.readValue(Double::class.java.classLoader) as Double,
        source.readString(),
        source.readParcelable<CommodityItem>(CommodityItem::class.java.classLoader),
        source.createStringArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(orderId)
        writeParcelable(userContact, 0)
        writeValue(orderAmount)
        writeString(receivingDate)
        writeParcelable(orderCommodity, 0)
        writeStringList(imageName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserOrder> = object : Parcelable.Creator<UserOrder> {
            override fun createFromParcel(source: Parcel): UserOrder = UserOrder(source)
            override fun newArray(size: Int): Array<UserOrder?> = arrayOfNulls(size)
        }
    }
}


data class UserPayment(
    val paymentId: String?,
    val userContact: UserContact?,
    val paymentAmount: Double = 0.0,
    val paymentDate: String?,
    val paymentMode: String?
) : Searchable(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(UserContact::class.java.classLoader),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(paymentId)
        parcel.writeParcelable(userContact, flags)
        parcel.writeDouble(paymentAmount)
        parcel.writeString(paymentDate)
        parcel.writeString(paymentMode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserPayment> {
        override fun createFromParcel(parcel: Parcel): UserPayment {
            return UserPayment(parcel)
        }

        override fun newArray(size: Int): Array<UserPayment?> {
            return arrayOfNulls(size)
        }
    }
}


enum class UserType(val userType: String) {
    LOADER("loader"),
    FARMER("farmer"),
    COMMISSION_AGENT("agent")
}