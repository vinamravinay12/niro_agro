package com.niro.niroapp.users.viewmodels.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import androidx.lifecycle.MutableLiveData
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.utils.NiroAppUtils
import com.niro.niroapp.viewmodels.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetContactsRepository(private val context : Context) {


     suspend fun getResponse(): MutableLiveData<APIResponse> {

        val responseData = MutableLiveData<APIResponse>(APILoader(true))

        responseData.value = try {
            val contactsList = getContactsFromPhoneContacts(context.contentResolver)
            Success(data = contactsList.toMutableList())
        } catch (exception : Exception) {
           APIError(404,NiroAppUtils.getDefaultErrorMessage(context))
        }

        return responseData
    }

    private suspend fun  getContactsFromPhoneContacts(contentResolver: ContentResolver): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        withContext(Dispatchers.IO) {
            val SORT_ORDER: String = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            val phones: Cursor? = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, SORT_ORDER)

            while (phones?.moveToNext() == true) {
                try {
                    val name: String = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    var phoneNumber: String = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER))

                    // Cleanup the phone number
                    phoneNumber = phoneNumber.replace("[()\\s-]+".toRegex(), "")
                    if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) continue
                    phoneNumber = NiroAppUtils.deleteCountryCode(phoneNumber)
                    val contact = Contact(name.trim(), phoneNumber.trim(), "")


                    if (!contactsList.contains(contact)) {
                        contactsList.add(contact)


                    }
                } catch (exception : Exception) {
                    continue
                }
            }
             phones?.close()
        }

        return contactsList
    }

}