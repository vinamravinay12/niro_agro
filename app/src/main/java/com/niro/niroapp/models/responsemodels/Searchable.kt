package com.niro.niroapp.models.responsemodels

import android.os.Parcel
import android.os.Parcelable
import com.niro.niroapp.users.fragments.ContactType

sealed class Searchable {}

data class User(
    val id: String?,
    val fullName: String?,
    val phoneNumber: String?,
    val businessName: String?,
    var selectedCommodities: List<CommodityItem>?,
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
        writeTypedList(selectedCommodities)
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


data class Contact(var name: String?, val number: String?, val email: String?) : Searchable(),
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
    val contactName: String?,
    val phoneNumber: String?,
    val userLocation: MandiLocation?,
    val selectedCommodity: List<CommodityItem>?,
    val businessName: String?,
    val avgRatings: Double = 0.0,
    val totalOrderAmount: Double = 0.0,
    val totalPaymentsDone: Double = 0.0,
    var contactType: String? = ContactType.MY_BUYERS.type
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readParcelable<MandiLocation>(MandiLocation::class.java.classLoader),
        source.createTypedArrayList(CommodityItem.CREATOR),
        source.readString(),
        source.readDouble(),
        source.readDouble(),
        source.readDouble(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(contactId)
        writeString(contactName)
        writeString(phoneNumber)
        writeParcelable(userLocation, 0)
        writeTypedList(selectedCommodity)
        writeString(businessName)
        writeDouble(avgRatings)
        writeDouble(totalOrderAmount)
        writeDouble(totalPaymentsDone)
        writeString(contactType)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserContact> = object : Parcelable.Creator<UserContact> {
            override fun createFromParcel(source: Parcel): UserContact = UserContact(source)
            override fun newArray(size: Int): Array<UserContact?> = arrayOfNulls(size)
        }
    }

    fun getCommoditiesString() : String {
       return selectedCommodity?.joinToString(separator = ",") { it -> "${it.name}"} ?: ""
    }
}


data class UserOrder(
    val orderId: String?,
    val userContact: UserContact?,
    val orderAmount: Double = 0.0,
    val receivingDate: String?,
    val orderCommodity: List<CommodityItem>?,
    val imageNames: List<String>?,
    val orderNumber: String?,
    val createdOn: CreatedOn?
) : com.niro.niroapp.models.responsemodels.Searchable(), Parcelable{
    constructor(source: Parcel) : this(
    source.readString(),
    source.readParcelable<UserContact>(UserContact::class.java.classLoader),
    source.readDouble(),
    source.readString(),
    source.createTypedArrayList(CommodityItem.CREATOR),
    source.createStringArrayList(),
    source.readString(),
    source.readParcelable<CreatedOn>(CreatedOn::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(orderId)
        writeParcelable(userContact, 0)
        writeDouble(orderAmount)
        writeString(receivingDate)
        writeTypedList(orderCommodity)
        writeStringList(imageNames)
        writeString(orderNumber)
        writeParcelable(createdOn, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserOrder> = object : Parcelable.Creator<UserOrder> {
            override fun createFromParcel(source: Parcel): UserOrder = UserOrder(source)
            override fun newArray(size: Int): Array<UserOrder?> = arrayOfNulls(size)
        }
    }
}


data class CreatedOn(
    val _seconds: Long?,
    val _nanoseconds: Long?
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readValue(Long::class.java.classLoader) as Long?,
        source.readValue(Long::class.java.classLoader) as Long?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(_seconds)
        writeValue(_nanoseconds)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CreatedOn> = object : Parcelable.Creator<CreatedOn> {
            override fun createFromParcel(source: Parcel): CreatedOn = CreatedOn(source)
            override fun newArray(size: Int): Array<CreatedOn?> = arrayOfNulls(size)
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
    ) {}

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


data class MandiRatesRecord(
    val arrival_date: String?,
    val commodity: String?,
    val district: String?,
    val market: String?,
    val max_price: String?,
    val min_price: String?,
    val state: String?,
    val timestamp: String?,
    val variety: String?,
    val modal_price: String?
) : Searchable(), Parcelable{
    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(arrival_date)
        writeString(commodity)
        writeString(district)
        writeString(market)
        writeString(max_price)
        writeString(min_price)
        writeString(state)
        writeString(timestamp)
        writeString(variety)
        writeString(modal_price)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MandiRatesRecord> =
            object : Parcelable.Creator<MandiRatesRecord> {
                override fun createFromParcel(source: Parcel): MandiRatesRecord =
                    MandiRatesRecord(source)

                override fun newArray(size: Int): Array<MandiRatesRecord?> = arrayOfNulls(size)
            }
    }
}


data class BuyCommodity(
    val userDetails: User?,
    val quantityAvailable: String?,
    val quantityType: String?,
    val price: Double?,
    val dispatchDate: String?,
    val createdOn: CreatedOn?,
    val commodity: String?,
    val images: List<String>?
) : Searchable(), Parcelable {
    constructor(source: Parcel) : this(
        source.readParcelable<User>(User::class.java.classLoader),
        source.readString(),
        source.readString(),
        source.readValue(Double::class.java.classLoader) as Double?,
        source.readString(),
        source.readParcelable<CreatedOn>(CreatedOn::class.java.classLoader),
        source.readString(),
        source.createStringArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(userDetails, 0)
        writeString(quantityAvailable)
        writeString(quantityType)
        writeValue(price)
        writeString(dispatchDate)
        writeParcelable(createdOn, 0)
        writeString(commodity)
        writeStringList(images)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BuyCommodity> =
            object : Parcelable.Creator<BuyCommodity> {
                override fun createFromParcel(source: Parcel): BuyCommodity =
                    BuyCommodity(source)

                override fun newArray(size: Int): Array<BuyCommodity?> = arrayOfNulls(size)
            }
    }
}


enum class UserType(val userType: String) {
    LOADER("loader"),
    FARMER("farmer"),
    COMMISSION_AGENT("agent")
}