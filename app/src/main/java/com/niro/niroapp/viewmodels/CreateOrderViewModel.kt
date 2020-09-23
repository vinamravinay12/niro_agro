package com.niro.niroapp.viewmodels

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.postdatamodels.CreateOrderPostData
import com.niro.niroapp.models.postdatamodels.SubmitRatingsPostData
import com.niro.niroapp.models.responsemodels.CommodityItem
import com.niro.niroapp.users.fragments.ContactType
import com.niro.niroapp.utils.DateUtils
import com.niro.niroapp.utils.NiroAppConstants
import com.niro.niroapp.viewmodels.repositories.CreateOrderRepository
import com.niro.niroapp.viewmodels.repositories.RatingsRepository

class CreateOrderViewModel(currentUserId: String?) : ViewModel() {

    private val selectedContactId = MutableLiveData<String>()
    private val selectedContactType = MutableLiveData<String>()
    private val mCurrentUserId = MutableLiveData<String>()
    private val orderAmount = MutableLiveData<String>()
    private val receivingDate = MutableLiveData<String>()
    private val selectedCommodity = MutableLiveData<ArrayList<CommodityItem>>(ArrayList())
    private val orderImages =
        MutableLiveData<MutableList<String>>(ArrayList<String>().toMutableList())
    private val orderImagesAbsolutePath =
        MutableLiveData<MutableList<String>>(ArrayList<String>().toMutableList())

    private val selectedCommodityDisplayName = MediatorLiveData<String>()

    private val selectedDateDisplayValue = MediatorLiveData<String>()

    init {
        mCurrentUserId.value = currentUserId
    }

    fun getOrderAmount() = orderAmount
    fun getReceivingDate() = receivingDate
    fun getOrderImages() = orderImages
    fun getSelectedCommodity() = selectedCommodity
    fun getSelectedContactId() = selectedContactId
    fun getSelectedContactType() = selectedContactType
    fun getCurrentUserId() = mCurrentUserId
    fun getOrderImagesAbsolutePath() = orderImagesAbsolutePath

    fun getSelectedCommodityDisplayName(): MediatorLiveData<String> {
        if (selectedCommodity.value?.isEmpty() == true) return selectedCommodityDisplayName
        selectedCommodityDisplayName.removeSource(selectedCommodity)
        selectedCommodityDisplayName.addSource(selectedCommodity) {
            if (!it.isNullOrEmpty()) {
                if (it != null) {
                    val commodityItem = it[0]
                    selectedCommodityDisplayName.value = "${commodityItem.name}"
                }
            }

        }

        return selectedCommodityDisplayName
    }


    fun getOrderAmountDisplayValue() = orderAmount
    fun getReceivingDateDisplayValue() = selectedDateDisplayValue.apply {
        if (receivingDate.value.isNullOrEmpty()) ""
        else {
            addSource(receivingDate) {
                selectedDateDisplayValue.value = DateUtils.convertDate(
                    receivingDate.value,
                    NiroAppConstants.POST_DATE_FORMAT,
                    NiroAppConstants.DISPLAY_DATE_FORMAT
                )
                removeSource(receivingDate)
            }

        }
    }


    fun validateOrderAmount(): MutableLiveData<Int> {
        return when {
            orderAmount.value.isNullOrEmpty() -> MutableLiveData(R.string.order_amount_empty)
            else -> MutableLiveData(-1)
        }
    }


    fun validateSelectedCommodity(): MutableLiveData<Int> {
        return when (selectedCommodity.value) {
            null -> MutableLiveData(R.string.commodity_empty_error)
            else -> MutableLiveData(-1)
        }
    }

    fun validateOrderImages(): MutableLiveData<Int> {
        return if (orderImages.value.isNullOrEmpty()) MutableLiveData(R.string.order_image_empty)
        else MutableLiveData(-1)
    }


    fun validateAllFields(): Int? {
        return when {
            validateOrderAmount().value ?: 0 > 0 -> validateOrderAmount().value
            validateSelectedCommodity().value ?: 0 > 0 -> validateSelectedCommodity().value
            else -> -1
        }

    }


    fun createOrder(context: Context?): MutableLiveData<APIResponse> {
        val createOrderPostData = CreateOrderPostData(
            currentUserId = mCurrentUserId.value,
            selectedContactId = selectedContactId.value,
            orderAmount = orderAmount.value?.toDouble() ?: 0.0,
            receivingDate = receivingDate.value,
            orderCommodity = selectedCommodity.value,
            imageName = orderImages.value?.toList(),
            contactType = selectedContactType.value ?: ContactType.MY_LOADERS.type
        )

        return CreateOrderRepository(createOrderPostData, context).getResponse()

    }


    fun updateRatings(rating: Float?, context: Context?): MutableLiveData<APIResponse> {
        val submitRatingsPostData = SubmitRatingsPostData(
            currentUserId = mCurrentUserId.value, selectedContactId = selectedContactId.value,
            ratings = rating, contactType = selectedContactType.value
        )

        return RatingsRepository(submitRatingsPostData, context).getResponse()
    }


    fun resetAllFields() {
        mCurrentUserId.value = ""
        receivingDate.value = ""
        orderAmount.value = ""
        selectedContactId.value = null
        selectedContactType.value = null
        selectedCommodity.value = null
        orderImages.value?.clear()
    }
}