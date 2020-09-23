package com.niro.niroapp.users.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.viewmodels.repositories.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetCurrentUserContactsRepository(private val currentUserId : String,context : Context?) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {

        val responseData = MutableLiveData<APIResponse>(APILoader(true))

        getAPIInterface()?.getContacts(currentUserId)?.enqueue(object :
            Callback<GenericAPIResponse<List<UserContact>>> {

            override fun onFailure(call: Call<GenericAPIResponse<List<UserContact>>>, t: Throwable) {
                responseData.value = APIError(404,getDefaultErrorMessage())
            }

            override fun onResponse(call: Call<GenericAPIResponse<List<UserContact>>>, response: Response<GenericAPIResponse<List<UserContact>>>) {

                if(!response.isSuccessful) {
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

                responseData.value = Success(response.body()?.data)
            }

        })

        return responseData
    }
}