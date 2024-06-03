package com.example.recylerv.ui.addeditcontacts

import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recylerv.R
import com.example.recylerv.data.Contacts
import com.example.recylerv.databinding.FragmentAddEditContactsBinding
import com.example.recylerv.ui.addeditcontacts.*
import com.example.recylerv.ui.contacts.ContactsAdapter
import com.example.recylerv.util.exhaustive
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditContactsFragment: Fragment(R.layout.fragment_add_edit_contacts){
    private val viewModel: AddEditContactsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditContactsBinding.bind(view)

        binding.apply {
            etName.setText(viewModel.contactName)
            etPhoneNum.setText(viewModel.contactPhNumber)

            etName.addTextChangedListener {
                viewModel.contactName = it.toString()
            }

            etPhoneNum.addTextChangedListener {
                viewModel.contactPhNumber = it.toString()
            }

            fabSaveContacts.setOnClickListener {

                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditContactEvent.collect{ event ->
                when (event){
                    is AddEditContactsViewModel.AddEditContactEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditContactsViewModel.AddEditContactEvent.NavigateBackWithResult -> {
                        binding.etName.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive

            }
        }

//        btnSave.setOnClickListener {
//            val name = et_name.text.toString()
//            val phoneNum = et_phoneNum.text.toString()
//
//            val newContact = Contacts(name, phoneNum)
//            val action = AddEditContactsFragmentDirections.actionAddEditContactsFragmentToContactsFragment2(newContact, name, phoneNum, true)
//
//            findNavController().navigate(action)
//        }
    }
}