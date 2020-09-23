package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.LoginResponse
import com.niro.niroapp.network.ApiClient
import com.niro.niroapp.utils.NiroAppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


abstract class Repository(private val context : Context?){
    abstract fun getResponse() : MutableLiveData<APIResponse>

    fun getDefaultErrorMessage() = NiroAppUtils.getDefaultErrorMessage(context)

    fun getAPIInterface() = ApiClient.getAPIInterface(context)
}


class LoginRepository<T>(private val phoneNumber : String,context : Context?)  : Repository(context){


    override fun getResponse(): MutableLiveData<APIResponse> {
       val responseData = MutableLiveData<APIResponse>()

        val loader : APIResponse = APILoader(true)
        getAPIInterface()?.login(phoneNumber)?.enqueue(object : Callback<LoginResponse> {

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("TAGSS","failure " + t.message)
               val response = APIError(404,getDefaultErrorMessage())
                responseData.value = response
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

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

                val success = Success<T>(response.body() as? T)
                responseData.value = success
            }

        })

        responseData.value = loader
        return responseData

    }
}