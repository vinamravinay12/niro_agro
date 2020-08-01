package com.niro.niroapp.viewmodels.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.postdatamodels.CreatePaymentPostData
import com.niro.niroapp.models.responsemodels.GenericAPIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddPaymentRepository(
    private val createPaymentPostData: CreatePaymentPostData,
    context: Context?
) : Repository(context) {

    override fun getResponse(): MutableLiveData<APIResponse> {

        val responseData: MutableLiveData<APIResponse> = MutableLiveData(APILoader(true))

        getAPIInterface()?.createPayment(createPaymentPostData)
            ?.enqueue(object : Callback<GenericAPIResponse<String>> {

                override fun onFailure(call: Call<GenericAPIResponse<String>>, t: Throwable) {
                    responseData.value = APIError(404, getDefaultErrorMessage())
                }

                override fun onResponse(
                    call: Call<GenericAPIResponse<String>>,
                    response: Response<GenericAPIResponse<String>>
                ) {

                    if (response.body()?.success != true) {
                        responseData.value = APIError(
                            response.code(),
                            errorMessage = response.body()?.message ?: getDefaultErrorMessage()
                        )
                        return
                    }

                    responseData.value = Success(data = response.body()?.data)
                }
            })
        return responseData
    }
}