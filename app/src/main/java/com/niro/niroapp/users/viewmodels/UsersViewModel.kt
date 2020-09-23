package com.niro.niroapp.users.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niro.niroapp.R
import com.niro.niroapp.models.APIResponse
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserContact
import com.niro.niroapp.users.adapters.UsersAdapter
import com.niro.niroapp.users.fragments.ContactType
import com.niro.niroapp.users.viewmodels.repositories.GetAllBuyersContactRepository
import com.niro.niroapp.users.viewmodels.repositories.GetContactsRepository
import com.niro.niroapp.users.viewmodels.repositories.GetCurrentUserContactsRepository

import com.niro.niroapp.utils.FilterResultsListener
import com.niro.niroapp.viewmodels.ListViewModel

class UsersViewModel(currentUser : User?) : ListViewModel(),FilterResultsListener<UserContact> {

    private var contactsList = MutableLiveData<MutableList<UserContact>>()
    private var currentUserData =  MutableLiveData<User>()
    private var adapter : UsersAdapter
    private var selectedContactType = MutableLiveData<String>(ContactType.MY_LOADERS.type)

    init {
        currentUserData.value = currentUser
        adapter = UsersAdapter(getViewType(),contactsList.value,this)
    }

    fun getContactsList() = contactsList

    fun getSelectedContactType() = selectedContactType

    fun getAdapter() = adapter


    override fun getViewType(): Int = R.layout.card_user

    override fun updateList() = adapter.updateList(contactsList.value)


    override fun onResultsFiltered(filteredList: List<UserContact>) {

        contactsList.value?.clear()
        contactsList.value = filteredList.toMutableList()
        adapter.updateFilterList(filteredList.toMutableList())
    }

    fun filterByLocation() {

        val sortedByLocation = contactsList.value?.sortedBy { userContact -> userContact.userLocation?.market }
        contactsList.value = sortedByLocation?.toMutableList()
        updateList()
    }

    fun filterByRatings() {
        contactsList.value = contactsList.value?.sortedByDescending { userContact -> userContact.avgRatings}?.toMutableList()
        updateList()
    }

    fun filterByCommodity() {

       contactsList.value =  contactsList.value?.sortedBy { userContact ->

            if(!userContact.selectedCommodity.isNullOrEmpty()) userContact.selectedCommodity[0].name
            else userContact.contactName
        }?.toMutableList()

        updateList()
    }

    fun getUsersList(context : Context?) : MutableLiveData<APIResponse>? {
        return currentUserData.value?.id?.let { GetCurrentUserContactsRepository(it,context).getResponse() }
    }


    fun getAllUsersList(context: Context?) : MutableLiveData<APIResponse>? = GetAllBuyersContactRepository(context).getResponse()


}

