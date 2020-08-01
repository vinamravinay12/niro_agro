package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.CategoryResponse
import com.niro.niroapp.models.responsemodels.MandiLocationResponse
import com.niro.niroapp.network.ApiInterface
import com.niro.niroapp.utils.NIroAppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MandiLocationRepository<T>(context : Context?) : Repository(context) {


    override fun getResponse(): MutableLiveData<APIResponse> {

        val responseData = MutableLiveData<APIResponse>()

        val loader = APILoader(true)

        getAPIInterface()?.mandiLocation?.enqueue(object : Callback<MandiLocationResponse> {
            override fun onFailure(call: Call<MandiLocationResponse>, t: Throwable) {
                val error = APIError(404, getDefaultErrorMessage())
                responseData.value = error
            }

            override fun onResponse(call: Call<MandiLocationResponse>, response: Response<MandiLocationResponse>) {
                if(response.body()?.success != true) {
                    val error = APIError(response.code(),response.body()?.message ?: getDefaultErrorMessage())
                    responseData.value = error
                    return
                }

                val success = Success<T>(response.body()?.data as? T)
                responseData.value = success
            }

        })

        responseData.value = loader
        return responseData
    
    }
}