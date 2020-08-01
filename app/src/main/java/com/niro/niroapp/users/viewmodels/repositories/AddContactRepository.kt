package com.niro.niroapp.users.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.users.models.AddContactPostData
import com.niro.niroapp.viewmodels.repositories.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddContactRepository(private val addContactPostData: AddContactPostData, context : Context?) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {

        val responseData = MutableLiveData<APIResponse>(APILoader(true))

        getAPIInterface()?.createContact(addContactPostData)?.enqueue(object : Callback<GenericAPIResponse<String>> {

            override fun onFailure(call: Call<GenericAPIResponse<String>>, t: Throwable) {
               responseData.value = APIError(404, getDefaultErrorMessage())
            }

            override fun onResponse(call: Call<GenericAPIResponse<String>>,
                response: Response<GenericAPIResponse<String>>) {

                if(response.body()?.success != true) {
                    responseData.value = APIError(response.code(),response.body()?.message ?: getDefaultErrorMessage())
                    return
                }

                responseData.value = Success<String>(response.body()?.data)
            }

        })
        return responseData
    }
}