package com.niro.niroapp.network;


import com.niro.niroapp.models.postdatamodels.SignupPostData;
import com.niro.niroapp.models.responsemodels.CategoryResponse;
import com.niro.niroapp.models.responsemodels.LoginResponse;
import com.niro.niroapp.models.responsemodels.MandiLocationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {


    @GET(NiroAPI.LOGIN)
    Call<LoginResponse> login(@Path("phoneNumber") String phoneNumber);

    @GET(NiroAPI.CATEGORIES)
    Call<CategoryResponse> getCategories();

    @GET(NiroAPI.MANDI_LOCATIONS)
    Call<MandiLocationResponse> getMandiLocation();

    @POST(NiroAPI.SIGN_UP)
    Call<LoginResponse> signup(@Body SignupPostData signupPostData);
    

  /*  @POST(API.LOGIN)
    Call<LoginResponse> login(@Body LoginData loginData);

    @POST(API.APPVERSION)
    Call<JsonElement> checkVersion(@Body JsonObject OBJ);

    @POST(API.GET_CATEGORY)
    Call<JsonElement> getCategory(@Body JsonObject OBJ);

    @POST(API.GET_DISH)
    Call<JsonElement> getDish(@Body JsonObject OBJ);

    @PUT(API.UPDATE_PROFILE)
    Call<JsonElement> profileUpdate(@Body JsonObject OBJ, @Header(API.AUTHORIZATION) String token);

    @GET(API.LOGOUT)
    Call<JsonElement> logoutApplication(@Header(API.AUTHORIZATION) String token);

    @POST(API.GET_NEW_ORDER)
    Call<OrderResponse> getNewOrder(@Body GetOrderPostData orderPostData, @Header(API.AUTHORIZATION) String token);

    @POST(API.GET_PROCESSING_ORDER)
    Call<OrderResponse> getProcessingOrder(@Body GetOrderPostData orderPostData, @Header(API.AUTHORIZATION) String token);

    @POST(API.GET_SERVED_ORDER)
    Call<OrderResponse> getServedOrder(@Body GetOrderPostData orderPostData, @Header(API.AUTHORIZATION) String token);

    @POST(API.ACCEPT_ORDER)
    Call<JsonElement> acceptOrder(@Body JsonObject OBJ, @Header(API.AUTHORIZATION) String token);

    @POST(API.REJECT_ORDER)
    Call<JsonElement> rejectOrder(@Body JsonObject OBJ, @Header(API.AUTHORIZATION) String token);

    @POST(API.DELIVERD_ORDER)
    Call<JsonElement> deliverOrder(@Body JsonObject OBJ, @Header(API.AUTHORIZATION) String token);

    @POST(API.SEARCH_ORDER)
    Call<OrderResponse> searchOrder(@Body JsonObject OBJ, @Header(API.AUTHORIZATION) String token);

    @POST(API.ORDER_STATUS)
    Call<OrderResponse> checkOrderStatus(@Body JsonObject OBJ, @Header(API.AUTHORIZATION) String token);
*/
}

