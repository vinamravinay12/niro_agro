package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.postdatamodels.CreateOrderPostData
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateOrderRepository(private val createOrderPostData: CreateOrderPostData, context: Context?) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {

        var apiResponse: MutableLiveData<APIResponse> = MutableLiveData(APILoader(true))

        getAPIInterface()?.createOrder(createOrderPostData)?.enqueue(object:
            Callback<GenericAPIResponse<String>> {

            override fun onFailure(call: Call<GenericAPIResponse<String>>, t: Throwable) {
                apiResponse.value = APIError(404,getDefaultErrorMessage())
            }

            override fun onResponse(call: Call<GenericAPIResponse<String>>, response: Response<GenericAPIResponse<String>>) {

                if(response.body()?.success != true) {
                    try {
                        val errorResponse = Gson().fromJson<GenericAPIResponse<Any>>(
                            response.errorBody()?.charStream(),
                            GenericAPIResponse::class.java
                        )
                        apiResponse.value = APIError(
                            response.code(),
                            if (!errorResponse.message.isNullOrEmpty()) errorResponse.message else getDefaultErrorMessage()
                        )
                        return
                    } catch (exception : Exception) {
                        apiResponse.value = APIError(response.code(), getDefaultErrorMessage())

                    }
                }

                apiResponse.value = Success(data = response.body()?.data)
            }
        })

        return apiResponse
    }
}