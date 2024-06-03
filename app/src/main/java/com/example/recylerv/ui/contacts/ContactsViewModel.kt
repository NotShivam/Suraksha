package com.example.recylerv.ui.contacts

import android.content.Context
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.recylerv.data.Contacts
import com.example.recylerv.data.ContactsDao
import com.example.recylerv.ui.ADD_CONTACT_RESULT_OK
import com.example.recylerv.ui.EDIT_CONTACT_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsDao: ContactsDao,
)  : ViewModel() {

    val contacts = contactsDao.getData().asLiveData()
    private val contactsEventChannel = Channel<ContactsEvent>()
    val contactsEvent = contactsEventChannel.receiveAsFlow()

    fun onContactSelected(contact: Contacts) = viewModelScope.launch {
        contactsEventChannel.send(ContactsEvent.NavigateToEditContactsScreen(contact))
    }
    fun onAddNewContactClick() = viewModelScope.launch {
        val totalContacts = contactsDao.getContactsCount()

        if(totalContacts == 5){
            contactsEventChannel.send(ContactsEvent.ShowMaxContactLimitReachedMessage)
        }else{
            contactsEventChannel.send(ContactsEvent.NavigateToAddContactsScreen)
        }
    }

    fun onAddEditResult(result: Int){
        when(result) {
            ADD_CONTACT_RESULT_OK -> showContactSavedConfirmationMessage("Contact added")
            EDIT_CONTACT_RESULT_OK -> showContactSavedConfirmationMessage("Contact updated")
        }
    }

    private fun showContactSavedConfirmationMessage(text: String) = viewModelScope.launch {
        contactsEventChannel.send(ContactsEvent.ShowContactSavedConfirmationMessage(text))
    }

    fun onSendSmsClick() = viewModelScope.launch {
        val phNumList = contactsDao.getphNum()
        contactsEventChannel.send(ContactsEvent.SendSmsToContacts(phNumList))
    }

    fun onContactSwiped(contact: Contacts) = viewModelScope.launch{
        contactsDao.delete(contact)
        contactsEventChannel.send(ContactsEvent.ShowUndoDeleteContactMessage(contact))
    }

    fun onUndoDeleteClick(contact: Contacts) = viewModelScope.launch {
        contactsDao.insert(contact)
    }

    sealed class ContactsEvent{
        data class ShowUndoDeleteContactMessage(val contact: Contacts): ContactsEvent()
        object NavigateToAddContactsScreen: ContactsEvent()
        object ShowMaxContactLimitReachedMessage: ContactsEvent()
        data class NavigateToEditContactsScreen(val contacts: Contacts): ContactsEvent()
        data class ShowContactSavedConfirmationMessage(val msg: String): ContactsEvent()
        data class SendSmsToContacts(val phNumList: List<String>): ContactsEvent()
    }
}