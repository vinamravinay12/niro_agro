package com.niro.niroapp.viewmodels.repositories

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.LoginResponse
import com.niro.niroapp.network.ApiInterface
import com.niro.niroapp.utils.NIroAppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


interface Repository {
    fun getResponse(apiInterface : ApiInterface) : MutableLiveData<APIResponse>
}


class LoginRepository<T>(private val phoneNumber : String)  : Repository{


    override fun getResponse(apiInterface: ApiInterface): MutableLiveData<APIResponse> {
       val responseData = MutableLiveData<APIResponse>()

        val loader : APIResponse = APILoader(true)
        apiInterface.login(phoneNumber).enqueue(object : Callback<LoginResponse> {

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
               val response = APIError(404,NIroAppConstants.SWW)
                responseData.value = response
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.body()?.success != true) {
                    val error = APIError(response.code(),response.body()?.message ?: NIroAppConstants.SWW )
                    responseData.value = error
                    return
                }

                val success = Success<T>(response.body() as? T)
                responseData.value = success
            }

        })

        responseData.value = loader
        return responseData

    }
}