package com.niro.niroapp.network

object NiroAPI {


    const val FILE_UPLOAD_URL = "gs://niroapp.appspot.com/"
    const val FIND_USER_BY_NUMBER = "/api/user/findUser/{phoneNumber}"
    const val LOGIN = "/api/user/login/{phoneNumber}"
    const val SIGN_UP = "/api/user/signup"
    const val CATEGORIES = "/api/data/commodity"
    const val MANDI_LOCATIONS = "/api/data/mandiLocation"

    const val CREATE_CONTACT = "/api/contacts/create"
    const val GET_CONTACTS = "/api/contacts/getContacts/{userId}"
    const val GET_ALL_CONTACTS = "/api/contacts/getAllContacts"
    const val UPDATE_RATINGS = "/api/contacts/updateRatings"

    const val ORDER_SUMMARY = "/api/orders/ordersSummary/{userId}"
    const val CREATE_ORDER = "/api/orders/createOrder"
    const val GET_ORDERS = "/api/orders/getOrders/{userId}"
    const val GET_ORDERS_FOR_CONTACT = "/api/orders/getOrdersForContact"


    const val CREATE_PAYMENT = "/api/payments/createPayment"
    const val GET_PAYMENTS = "/api/payments/getPayments/{userId}"
    const val GET_PAYMENTS_FOR_CONTACT = "/api/payments/getPaymentsForContact"

    const val CREATE_LOAN_REQUIREMENT = "/api/loans/create"

    const val UPDATE_COMMODITIES = "/api/user/updateCommodities"
    const val UPDATE_USER_PROFILE = "/api/user/updateProfile"

    const val GET_LIVE_MANDI_RATE = "/api/data/rates/mandiRates"

    const val CREATE_SELL_ORDER = "/api/sellers/createSellOrder"
    const val GET_ALL_SELL_ORDERS = "/api/sellers/getBuyOrders"
}