package com.hayakai.ui.newcontact

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.NewContactDto
import com.hayakai.data.repository.ContactRepository

class NewContactViewModel(
    private val contactRepository: ContactRepository
) : ViewModel() {
    fun newContact(newContactDto: NewContactDto) =
        contactRepository.newContact(newContactDto)
}