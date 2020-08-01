package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.models.responsemodels.UserPayment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetPaymentsRepository(private val currentUserId : String?,context : Context?) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {
        val responseData : MutableLiveData<APIResponse> = MutableLiveData(APILoader(true))

        getAPIInterface()?.getPayments(currentUserId)?.enqueue(object :
            Callback<GenericAPIResponse<UserPayment>> {

            override fun onFailure(call: Call<GenericAPIResponse<UserPayment>>, t: Throwable) {
                responseData.value = APIError(404,getDefaultErrorMessage())
            }

            override fun onResponse(call: Call<GenericAPIResponse<UserPayment>>, response: Response<GenericAPIResponse<UserPayment>>) {
                if(response.body()?.success != true) {
                    responseData.value = APIError(response.code(), response.message() ?: getDefaultErrorMessage())
                    return
                }

                responseData.value = Success<UserPayment>(response.body()?.data)
            }
        })

        return responseData

    }
}