package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.statekeeper.consume
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposetest.core.componentScope
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.AddContactUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DefaultAddContactComponent(
    componentContext: ComponentContext,
    val onContactSaved: () -> Unit
) : AddContactComponent,
    ComponentContext by componentContext {

    private val store: AddContactStore = instanceKeeper.getStore {
        val storeFactory = AddContactStoreFactory()
        storeFactory.create()
    }

    init {
        componentScope().launch {
            store.labels.collect {
                when (it) {
                    AddContactStore.Label.ContactSaved -> {
                        onContactSaved()
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AddContactStore.State>
        get() = store.stateFlow

    override fun onUsernameChanged(username: String) {
        store.accept(AddContactStore.Intent.ChangeUsername(username))
    }

    override fun onPhoneChanged(phone: String) {
        store.accept(AddContactStore.Intent.ChangePhone(phone))
    }

    override fun onSaveContactClicked() {
        store.accept(AddContactStore.Intent.SaveContact)
    }

}