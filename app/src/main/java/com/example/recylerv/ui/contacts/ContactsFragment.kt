package com.example.recylerv.ui.contacts

import android.media.MediaRouter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recylerv.R
import com.example.recylerv.data.Contacts
import com.example.recylerv.databinding.FragmentContactsBinding
import com.example.recylerv.message.SendMessage
import com.example.recylerv.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts), ContactsAdapter.onItemClickListner{

    private val viewModel: ContactsViewModel by viewModels()
    lateinit var name: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentContactsBinding.bind(view)
        val adapter = ContactsAdapter(this)

        binding.apply {
            rvContacts.adapter = adapter
            rvContacts.layoutManager = LinearLayoutManager(requireContext())
            rvContacts.setHasFixedSize(true)

            fabAddContacts.setOnClickListener{
                viewModel.onAddNewContactClick()
            }

            fabSendSms.setOnClickListener {
                viewModel.onSendSmsClick()
            }

            ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                   val contact = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.onContactSwiped(contact)
                }

            }).attachToRecyclerView(rvContacts)

        }

        setFragmentResultListener("add_edit_request"){_, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }
        // trailing lamda ---> { } (can be used when lambda is last argument)
        viewModel.contacts.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.contactsEvent.collect{ event ->
                when (event) {
                    is ContactsViewModel.ContactsEvent.NavigateToAddContactsScreen -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToAddEditContactsFragment(
                                null,
                                "Add Contact"
                            )
                        findNavController().navigate(action)
                    }
                    is ContactsViewModel.ContactsEvent.NavigateToEditContactsScreen -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToAddEditContactsFragment(
                                event.contacts,
                                "Edit Contact"
                            )
                        findNavController().navigate(action)
                    }
                    is ContactsViewModel.ContactsEvent.ShowContactSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is ContactsViewModel.ContactsEvent.SendSmsToContacts -> {
                        val smsSender = SendMessage(requireActivity().application)
                        smsSender.onBtnClick(event.phNumList)
                    }
                    is ContactsViewModel.ContactsEvent.ShowUndoDeleteContactMessage -> {
                        Snackbar.make(requireView(), "Contact deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.contact)
                            }.show()
                    }
                    is ContactsViewModel.ContactsEvent.ShowMaxContactLimitReachedMessage -> {
                        Snackbar.make(requireView(), "Maximum contacts limit reached !", Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }
//    fun getTotalNumberOfContacts(): Int {
//        return adapter.itemCount
//    }
    override fun onItemCLick(contact: Contacts) {
        viewModel.onContactSelected(contact)
    }
}