package com.niro.niroapp.users.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.postdatamodels.GetMandiRatesPostData
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import com.niro.niroapp.models.responsemodels.MandiRatesRecord
import com.niro.niroapp.viewmodels.repositories.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetMandiRatesForDateRepository(
    private val mandiRatesPostData: GetMandiRatesPostData,
    context: Context?
) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {

        val responseData = MutableLiveData<APIResponse>(APILoader(true))


        getAPIInterface()?.getMandiRates(mandiRatesPostData)?.enqueue(object :
            Callback<GenericAPIResponse<List<MandiRatesRecord>>> {
            override fun onFailure(
                call: Call<GenericAPIResponse<List<MandiRatesRecord>>>,
                t: Throwable
            ) {
                responseData.value = APIError(404, getDefaultErrorMessage())
            }

            override fun onResponse(
                call: Call<GenericAPIResponse<List<MandiRatesRecord>>>,
                response: Response<GenericAPIResponse<List<MandiRatesRecord>>>
            ) {
                if (!response.isSuccessful) {
                    try {
                        val errorResponse = Gson().fromJson<GenericAPIResponse<Any>>(
                            response.errorBody()?.charStream(),
                            GenericAPIResponse::class.java
                        )
                        responseData.value = APIError(
                            response.code(),
                            if (errorResponse.message.isNotEmpty()) errorResponse.message else getDefaultErrorMessage()
                        )
                        return
                    } catch (exception: Exception) {
                        responseData.value = APIError(response.code(), getDefaultErrorMessage())
                    }
                }

                responseData.value = Success(response.body()?.data)
            }
        })

        return responseData
    }
}