package com.niro.niroapp.network;


import com.niro.niroapp.loans.models.LoanRequestPostData;
import com.niro.niroapp.models.postdatamodels.CreateOrderPostData;
import com.niro.niroapp.models.postdatamodels.CreatePaymentPostData;
import com.niro.niroapp.models.postdatamodels.CreateSellOrderPostData;
import com.niro.niroapp.models.postdatamodels.GetMandiRatesPostData;
import com.niro.niroapp.models.postdatamodels.GetSellerCommoditiesPostData;
import com.niro.niroapp.models.postdatamodels.SignupPostData;
import com.niro.niroapp.models.postdatamodels.SubmitRatingsPostData;
import com.niro.niroapp.models.postdatamodels.UpdateCommoditiesPostData;
import com.niro.niroapp.models.postdatamodels.UpdateProfilePostData;
import com.niro.niroapp.models.responsemodels.BuyCommodity;
import com.niro.niroapp.models.responsemodels.CategoryResponse;
import com.niro.niroapp.models.responsemodels.GenericAPIResponse;
import com.niro.niroapp.models.responsemodels.LoginResponse;
import com.niro.niroapp.models.responsemodels.MandiLocationResponse;
import com.niro.niroapp.models.responsemodels.MandiRatesRecord;
import com.niro.niroapp.models.responsemodels.OrderSummary;
import com.niro.niroapp.models.responsemodels.User;
import com.niro.niroapp.models.responsemodels.UserContact;
import com.niro.niroapp.models.responsemodels.UserOrder;
import com.niro.niroapp.models.responsemodels.UserPayment;
import com.niro.niroapp.users.models.AddContactPostData;
import com.niro.niroapp.users.models.GetOrdersForContactPostData;

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
    Call<GenericAPIResponse<OrderSummary>> getOrderSummary(@Path("userId") String userId);

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
    Call<GenericAPIResponse<List<UserOrder>>> getOrders(@Path("userId") String userId);

    @POST(NiroAPI.CREATE_PAYMENT)
    Call<GenericAPIResponse<String>> createPayment(@Body CreatePaymentPostData createPaymentPostData);

    @GET(NiroAPI.GET_PAYMENTS)
    Call<GenericAPIResponse<List<UserPayment>>> getPayments(@Path("userId") String userId);

    @POST(NiroAPI.CREATE_LOAN_REQUIREMENT)
    Call<GenericAPIResponse<String>> requestForLoan(@Body LoanRequestPostData loanRequestPostData);

    @PUT(NiroAPI.UPDATE_COMMODITIES)
    Call<GenericAPIResponse<User>> updateCommodities(@Body UpdateCommoditiesPostData updateCommoditiesPostData);

    @PUT(NiroAPI.UPDATE_USER_PROFILE)
    Call<GenericAPIResponse<User>> updateUserProfile(@Body UpdateProfilePostData updateProfilePostData);

    @PUT(NiroAPI.UPDATE_RATINGS)
    Call<GenericAPIResponse<String>> updateRatings(@Body SubmitRatingsPostData submitRatingsPostData);

    @POST(NiroAPI.GET_ORDERS_FOR_CONTACT)
    Call<GenericAPIResponse<List<UserOrder>>> getOrdersForContact(@Body GetOrdersForContactPostData ordersForContactPostData);

    @POST(NiroAPI.GET_PAYMENTS_FOR_CONTACT)
    Call<GenericAPIResponse<List<UserPayment>>> getPaymentsForContact(@Body GetOrdersForContactPostData paymentsForContactPostData);

    @POST(NiroAPI.GET_LIVE_MANDI_RATE)
    Call<GenericAPIResponse<List<MandiRatesRecord>>> getMandiRates(@Body GetMandiRatesPostData getMandiRatesPostData);

    @POST(NiroAPI.CREATE_SELL_ORDER)
    Call<GenericAPIResponse<String>> createSellOrder(@Body CreateSellOrderPostData createSellOrderPostData);


    @POST(NiroAPI.GET_ALL_SELL_ORDERS)
    Call<GenericAPIResponse<List<BuyCommodity>>> getSellerCommodities(@Body GetSellerCommoditiesPostData getSellerOrdersPostData);





    


}

