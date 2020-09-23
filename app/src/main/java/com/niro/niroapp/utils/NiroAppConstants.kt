package com.niro.niroapp.utils

import android.Manifest

object NiroAppConstants {

    const val MAX_ALLOWED_ATTEMPTS = 2
    const val KEY_NO_FAILED_UPDATES = "FailedUpdateCount"
    const val APP_UPDATE_REQUEST_CODE = 301
    const val KEY_MANDI_RECORDS = "MandiRecords"
    const val TAG_CONTACT_DETAIL_ORDER = "ContactDetailOrderFragment"
    const val TAG_CONTACT_DETAIL_PAYMENTS = "ContactDetailPaymentsFragment"
    const val ARG_SELECTED_ORDER = "ArgSelectedOrder"
    const val TAG_DIALOG_DATE = "DatePickerDialog"
    const val TAG_DIALOG_SUCCESS = "SuccessDialog"
    const val TAG_DIALOG_LOGOUT = "LogoutDialog"
    const val TAG_DIALOG_RATINGS = "DialogRatings"
    const val ARG_CURRENT_USER_ID = "ArgCurrentUserId"
    const val ARG_COMMODITIES_EDIT = "ArgCommoditiesEdit"
    const val PREFIX_ORDER_IMAGE = "order_"
    const val KEY_REQUEST_WRITE_STORAGE_PERMISSION = 203
    const val KEY_REQUEST_CAMERA_PERMISSION = 202
    const val KEY_REQUEST_CAPTURE_PHOTO = 201
    const val CODE_CAMERA_PERMISSION = Manifest.permission.CAMERA
    const val CODE_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val POST_DATE_FORMAT = "yyyy-MM-dd"
    const val DISPLAY_DATE_FORMAT = "dd MMM yyyy"
    const val ARG_USER_CONTACT = "ArgUserContact"
    const val ARG_USER_CONTACT_ID = "ArgUserContactId"
    const val ARG_USER_CONTACT_TYPE = "ArgUserContactType"
    const val ARG_NEXT_NAVIGATION_ID = "NextNavigationViewId"
    const val ARG_ALLOW_MULTISELECT = "ArgAllowMultiSelect"
    const val ARG_SELECTED_COMMODITIES = "ArgSelectedCommodities"
    const val ARG_SELECTED_MANDI = "ArgSelectedMandi"
    const val PREVIOUS_SCREEN_ID = "PreviousScreeniD"
    const val ARG_CONTACT = "ArgContacts"
    const val KEY_MULITPLE_PERMISSIONS = 101
    const val CODE_READ_CONTACTS = Manifest.permission.READ_CONTACTS
    const val ARG_CURRENT_USER = "CurrentUser"
    const val TAG_HOME = "HomeFragment"
    const val TAG_ORDERS = "OrdersFragment"
    const val TAG_PAYMENTS = "PaymentsFragment"
    const val TAG_USERS = "UsersFragment"
    const val TAG_LOANS = "LoansFragment"
    const  val TAG_DIALOG_CREATE_USER = "CreateUserDialog"
    const val TAG_CONTACTS = "ContactsFragment"
    const val TAG_ENTER_MANUAL = "EnterUserFragment"
    const val TAG_USER_TYPE = "UserTypeFragment"
    const  val TAG_MANDI_LIST = "MandiListFragment"
    const val TAG_COMMODITIES = "CommoditiesFragment"
    const val TAG_ENTER_BUSINESS = "EnterBusinessNameFragment"
    const val NOTIFICATION_SP = "NotificationSP"
    const val LOGIN_SP = "LoginSP"
    const val USER_TOKEN = "UserToken"
    const val USER_DATA = "UserData"
    const val TAG_ENTER_NAME = "EnterNameFragment"
    const val SWW = "Something Went Wrong"
    const val TAG_OTP = "OtpFragment"
    const val TAG_LOGIN = "LoginFragment"
    const val USER_FCMTOKENID = "FCM Token"
    const val USER_DEVICEID = "DeviceID"
}