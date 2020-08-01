package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.OrderSummary
import com.niro.niroapp.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersRepository(context: Context?) : Repository(context) {


    override fun getResponse(): MutableLiveData<APIResponse> {

        val responseData = MutableLiveData<APIResponse>(APILoader(true))

        getAPIInterface()?.orderSummary?.enqueue(object :
            Callback<GenericAPIResponse<OrderSummary>> {
            override fun onFailure(call: Call<GenericAPIResponse<OrderSummary>>, t: Throwable) {
                responseData.value = APIError(404, getDefaultErrorMessage())
            }

            override fun onResponse(
                call: Call<GenericAPIResponse<OrderSummary>>,
                response: Response<GenericAPIResponse<OrderSummary>>
            ) {
                if (response.body()?.success != true) {

                    responseData.value = APIError(
                        response.code(),
                        response.body()?.message ?: getDefaultErrorMessage()
                    )
                    return
                }

                responseData.value = Success(response.body()?.data)
            }


        })

        return responseData
    }
}