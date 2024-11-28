package com.hayakai.ui.detailcontact

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.DeleteContactDto
import com.hayakai.data.remote.dto.UpdateContactDto
import com.hayakai.data.repository.ContactRepository

class DetailContactViewModel(
    private val contactRepository: ContactRepository
) : ViewModel() {
    fun deleteContact(deleteContactDto: DeleteContactDto) =
        contactRepository.deleteContact(deleteContactDto)

    fun updateContact(updateContactDto: UpdateContactDto) =
        contactRepository.updateContact(updateContactDto)
}