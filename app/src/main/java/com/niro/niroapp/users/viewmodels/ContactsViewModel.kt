package com.niro.niroapp.users.viewmodels

import android.content.ContentResolver
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.Contact
import com.niro.niroapp.users.adapters.ContactListAdapter
import com.niro.niroapp.users.viewmodels.repositories.GetContactsRepository
import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.utils.ItemClickListener
import com.niro.niroapp.viewmodels.ListViewModel
import kotlinx.coroutines.*

class ContactsViewModel : ListViewModel(), FilterResultsListener<Contact>{

    private var contactsData = MutableLiveData<MutableList<Contact>>(ArrayList<Contact>().toMutableList())

    private var contactName = MediatorLiveData<String>()
    private var checked = MutableLiveData<Boolean>()
    private var contactNumber = MediatorLiveData<String>()
    private  var adapter: ContactListAdapter
    private var response = MutableLiveData<APIResponse>()

    var itemClickListener : ItemClickListener? = null


    init {
        adapter = ContactListAdapter(getViewType(), contactsData.value?.toMutableList(), this)
    }

    fun getResponse() = response

    fun getContactsData(): MutableLiveData<MutableList<Contact>> = contactsData


    fun isChecked(): MutableLiveData<Boolean> = checked

    fun getAdapter() = adapter


    fun getContactName(position: Int): MediatorLiveData<String> {
        contactName.value = contactsData.value?.get(position)?.name ?: ""
        return contactName
    }

    fun getContactNumber(position: Int): MediatorLiveData<String> {
        contactNumber.value = contactsData.value?.get(position)?.number ?: ""
        return contactNumber
    }


    fun fetchContacts(context: Context) {
        val contactsRepository = GetContactsRepository(context)


       viewModelScope.launch{
            val responseDataAsync =  async { contactsRepository.getResponse() }

           val responseData = responseDataAsync.await()
            withContext(Dispatchers.Main) {
                response.value = responseData.value
            }

        }


    }


    override fun getViewType(): Int = R.layout.card_contact

    override fun updateList() {
        adapter.updateList(contactsData.value)
    }



    override fun onResultsFiltered(filteredList: List<Contact>) {
        contactsData.value?.clear()
        contactsData.value = filteredList.toMutableList()
        adapter.updateFilterList(filteredList.toMutableList())
    }
}