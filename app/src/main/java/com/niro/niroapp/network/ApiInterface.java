package com.niro.niroapp.network;


import com.niro.niroapp.loans.models.LoanRequestPostData;
import com.niro.niroapp.models.postdatamodels.CreateOrderPostData;
import com.niro.niroapp.models.postdatamodels.CreatePaymentPostData;
import com.niro.niroapp.models.postdatamodels.SignupPostData;
import com.niro.niroapp.models.postdatamodels.UpdateCommoditiesPostData;
import com.niro.niroapp.models.postdatamodels.UpdateProfilePostData;
import com.niro.niroapp.models.responsemodels.Category;
import com.niro.niroapp.models.responsemodels.CategoryResponse;
import com.niro.niroapp.models.responsemodels.GenericAPIResponse;
import com.niro.niroapp.models.responsemodels.LoginResponse;
import com.niro.niroapp.models.responsemodels.MandiLocationResponse;
import com.niro.niroapp.models.responsemodels.OrderSummary;
import com.niro.niroapp.models.responsemodels.User;
import com.niro.niroapp.models.responsemodels.UserContact;
import com.niro.niroapp.models.responsemodels.UserOrder;
import com.niro.niroapp.models.responsemodels.UserPayment;
import com.niro.niroapp.users.models.AddContactPostData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET(NiroAPI.ORDER_SUMMARY)
    Call<GenericAPIResponse<OrderSummary>> getOrderSummary();

    @GET(NiroAPI.FIND_USER_BY_NUMBER)
    Call<GenericAPIResponse<User>> finUserByNumber(@Path("phoneNumber") String number);

    @POST(NiroAPI.CREATE_CONTACT)
    Call<GenericAPIResponse<String>> createContact(@Body AddContactPostData addContactPostData);

    @GET(NiroAPI.GET_CONTACTS)
    Call<GenericAPIResponse<List<UserContact>>> getContacts(@Path("userId") String userId);

    @GET(NiroAPI.GET_ALL_CONTACTS)
    Call<GenericAPIResponse<List<UserContact>>> getAllContacts();

    @POST(NiroAPI.CREATE_ORDER)
    Call<GenericAPIResponse<String>> createOrder(@Body CreateOrderPostData createOrderPostData);

    @GET(NiroAPI.GET_ORDERS)
    Call<GenericAPIResponse<UserOrder>> getOrders(@Path("userId") String userId);

    @POST(NiroAPI.CREATE_PAYMENT)
    Call<GenericAPIResponse<String>> createPayment(@Body CreatePaymentPostData createPaymentPostData);

    @GET(NiroAPI.GET_PAYMENTS)
    Call<GenericAPIResponse<UserPayment>> getPayments(@Path("userId") String userId);

    @POST(NiroAPI.CREATE_LOAN_REQUIREMENT)
    Call<GenericAPIResponse<String>> requestForLoan(@Body LoanRequestPostData loanRequestPostData);

    @PUT(NiroAPI.UPDATE_COMMODITIES)
    Call<GenericAPIResponse<User>> updateCommodities(@Body UpdateCommoditiesPostData updateCommoditiesPostData);

    @PUT(NiroAPI.UPDATE_USER_PROFILE)
    Call<GenericAPIResponse<User>> updateUserProfile(@Body UpdateProfilePostData updateProfilePostData);




    

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

