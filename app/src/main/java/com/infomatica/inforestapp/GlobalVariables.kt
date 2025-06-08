package com.infomatica.inforestapp

import javax.inject.Singleton

// GlobalVariables.kt
@Singleton
object GlobalVariables {
    var userToken: String = ""
    var isUserLoggedIn: Boolean = false
    var ordenGeneral: Int = 0
}