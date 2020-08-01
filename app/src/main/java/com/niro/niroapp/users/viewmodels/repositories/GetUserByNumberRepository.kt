package com.niro.niroapp.users.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.viewmodels.repositories.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetUserByNumberRepository(context : Context?, private val phoneNumber : String?) : Repository(context) {


    override fun getResponse(): MutableLiveData<APIResponse> {

        val responseData : MutableLiveData<APIResponse> = MutableLiveData(APILoader(true))

        getAPIInterface()?.finUserByNumber(phoneNumber)?.enqueue(object : Callback<GenericAPIResponse<User>> {

            override fun onFailure(call: Call<GenericAPIResponse<User>>, t: Throwable) {
                responseData.value = APIError(errorCode = 404, errorMessage = getDefaultErrorMessage())
            }

            override fun onResponse(call: Call<GenericAPIResponse<User>>, response: Response<GenericAPIResponse<User>>) {

                if(response.body()?.success != true) {
                    responseData.value = APIError(response.code(),response.body()?.message ?: getDefaultErrorMessage())
                    return
                }

                responseData.value = Success<User>(data = response.body()?.data)
            }

        })

        return responseData
    }


}