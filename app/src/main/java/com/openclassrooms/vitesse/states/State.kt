package com.openclassrooms.vitesse.states

sealed class State {
    object Idle : State()            // Not tried yet
    object Success : State()         // Successful connection
    object Loading : State()         // Loading
    sealed class Error : State() {
        object LoginError : Error()                 // Login error (id or password)
        object InsufficientBalance : Error()        // Insufficient balance
        object Server : Error()                     // Server error
        object NoInternet : Error()                 // No internet
    }
}