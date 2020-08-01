package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.postdatamodels.UpdateCommoditiesPostData
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.LoginResponse
import com.niro.niroapp.models.responsemodels.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateCommoditiesRepository(private val updateCommodities : UpdateCommoditiesPostData?, context: Context?) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {
        val responseData : MutableLiveData<APIResponse> = MutableLiveData<APIResponse>(APILoader(true))

        getAPIInterface()?.updateCommodities(updateCommodities)?.enqueue(object : Callback<GenericAPIResponse<User>> {

            override fun onFailure(call: Call<GenericAPIResponse<User>>, t: Throwable) {
                val response = APIError(404, getDefaultErrorMessage())
                responseData.value = response
            }

            override fun onResponse(call: Call<GenericAPIResponse<User>>, response: Response<GenericAPIResponse<User>>) {
                if(response.body()?.success != true) {
                    val error = APIError(response.code(),response.body()?.message ?: getDefaultErrorMessage() )
                    responseData.value = error
                    return
                }

                val success = Success<User>(response.body()?.data)
                responseData.value = success
            }

        })

        return responseData
    }
}