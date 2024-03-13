package com.hoxy.hlivv.ui.login.ui.login

/**
 * Authentication result : success (user details) or error message.
 */

/**
 * @author 반정현
 */
data class LoginResult(
    //val success: LoggedInUserView? = null,
    val success: Int? = null,
    val error: Int? = null
)