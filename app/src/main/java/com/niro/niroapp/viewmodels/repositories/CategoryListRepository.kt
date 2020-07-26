package com.niro.niroapp.viewmodels.repositories

import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.CategoryResponse
import com.niro.niroapp.network.ApiInterface
import com.niro.niroapp.utils.NIroAppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryListRepository<T> : Repository {


    override fun getResponse(apiInterface: ApiInterface): MutableLiveData<APIResponse> {
        val responseData = MutableLiveData<APIResponse>()

        val loader = APILoader(true)

        apiInterface.categories.enqueue(object : Callback<CategoryResponse> {
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                val error = APIError(404,NIroAppConstants.SWW)
                responseData.value = error
            }

            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if(response.body()?.success != true) {
                    val error = APIError(response.code(),response.body()?.message ?: NIroAppConstants.SWW)
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