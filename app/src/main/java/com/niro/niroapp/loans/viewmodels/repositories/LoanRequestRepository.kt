package com.niro.niroapp.loans.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.niro.niroapp.loans.models.LoanRequestPostData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.viewmodels.repositories.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoanRequestRepository(private val loanRequestPostData: LoanRequestPostData,context : Context?) : Repository(context) {


    override fun getResponse(): MutableLiveData<APIResponse> {
        var apiResponse: MutableLiveData<APIResponse> = MutableLiveData(APILoader(true))

        getAPIInterface()?.requestForLoan(loanRequestPostData)?.enqueue(object:
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