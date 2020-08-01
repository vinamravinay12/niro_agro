package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.UserOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetOrdersRepository(private val currentUserId  : String?,context: Context?) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {
        val responseData : MutableLiveData<APIResponse> = MutableLiveData(APILoader(true))

        getAPIInterface()?.getOrders(currentUserId)?.enqueue(object : Callback<GenericAPIResponse<UserOrder>> {

            override fun onFailure(call: Call<GenericAPIResponse<UserOrder>>, t: Throwable) {
                responseData.value = APIError(404,getDefaultErrorMessage())
            }

            override fun onResponse(call: Call<GenericAPIResponse<UserOrder>>, response: Response<GenericAPIResponse<UserOrder>>) {
                if(response.body()?.success != true) {
                    responseData.value = APIError(response.code(), response.message() ?: getDefaultErrorMessage())
                    return
                }

                responseData.value = Success<UserOrder>(response.body()?.data)
            }
        })

        return responseData
    }



}