package com.openclassrooms.vitesse.states

sealed class State {
    object Idle : State()           // Not tried yet
    object Success : State()        // Successful connection
    object Loading : State()        // Loading
    object Error : State()          // Error
}