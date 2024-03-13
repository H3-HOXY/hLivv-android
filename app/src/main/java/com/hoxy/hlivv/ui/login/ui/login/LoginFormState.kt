package com.hoxy.hlivv.ui.login.ui.login

/**
 * Data validation state of the login form.
 */
/**
 * @author 반정현
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)