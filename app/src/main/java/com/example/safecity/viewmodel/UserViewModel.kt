package com.example.safecity.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _userName = MutableStateFlow("Guardian")
    val userName: StateFlow<String> = _userName

    fun setUserName(name: String) {
        if (name.isNotBlank()) {
            _userName.value = name
        }
    }
}
