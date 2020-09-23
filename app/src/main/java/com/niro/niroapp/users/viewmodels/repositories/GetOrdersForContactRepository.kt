package com.niro.niroapp.users.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.UserOrder
import com.niro.niroapp.users.models.GetOrdersForContactPostData
import com.niro.niroapp.viewmodels.repositories.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetOrdersForContactRepository(private val ordersForContactPostData: GetOrdersForContactPostData,context: Context?) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {
        val responseData : MutableLiveData<APIResponse> = MutableLiveData(APILoader(true))

        getAPIInterface()?.getOrdersForContact(ordersForContactPostData)?.enqueue(object :
            Callback<GenericAPIResponse<List<UserOrder>>> {

            override fun onFailure(call: Call<GenericAPIResponse<List<UserOrder>>>, t: Throwable) {
                responseData.value = APIError(404,getDefaultErrorMessage())
            }

            override fun onResponse(call: Call<GenericAPIResponse<List<UserOrder>>>, response: Response<GenericAPIResponse<List<UserOrder>>>) {
                if(response.body()?.success != true) {
                    try {
                        val errorResponse = Gson().fromJson<GenericAPIResponse<Any>>(
                            response.errorBody()?.charStream(),
                            GenericAPIResponse::class.java
                        )
                        responseData.value = APIError(
                            response.code(),
                            if (!errorResponse.message.isNullOrEmpty()) errorResponse.message else getDefaultErrorMessage()
                        )
                        return
                    } catch (exception : Exception) {
                        responseData.value = APIError(response.code(), getDefaultErrorMessage())

                    }
                }

                responseData.value = Success<List<UserOrder>>(response.body()?.data)
            }
        })

        return responseData
    }



}