package com.sharifin.base

import android.content.Context
import androidx.annotation.StringRes
import com.sharifin.retrofit.ErrorHolder

data class BaseState(
        val stable: Boolean = true,
        val error: ErrorState = ErrorState.Disabled,
        val showLoading: Boolean = false,
        val showKeyboard: Boolean = false,
        val confirm: Boolean = false) {

    sealed class ErrorState(val showSnackBar: Boolean) {
        data class String(val snackBarMessageString: kotlin.String) : ErrorState(showSnackBar = true)
        data class Res(@StringRes val snackBarMessage: Int) : ErrorState(showSnackBar = true)
        data class UnAuthorized(val errorMessage: kotlin.String) : ErrorState(showSnackBar = false)
        object Disabled : ErrorState(showSnackBar = false)
    }

    companion object {
        @JvmStatic
        fun stable() = BaseState()

        @JvmStatic
        fun loading() = BaseState(stable = false, showLoading = true, showKeyboard = false)

        @JvmStatic
        fun showKeyboard() = BaseState(stable = false, showKeyboard = true)

        @JvmStatic
        fun showError(@StringRes message: Int) = BaseState(stable = false, error = ErrorState.Res(message), showKeyboard = false)

        @JvmStatic
        fun showError(message: String) = BaseState(stable = false, error = ErrorState.String(message), showKeyboard = false)

        @JvmStatic
        fun confirm() = BaseState(stable = false, confirm = true, showKeyboard = false)

        @JvmStatic
        fun unAuthorized(errorMessage: String) = BaseState(stable = false, error = ErrorState.UnAuthorized(errorMessage), showKeyboard = false)
    }
}

fun BaseState.ErrorState.getErrorString(context: Context): String {
    return when {
        this is BaseState.ErrorState.String -> return snackBarMessageString
        this is BaseState.ErrorState.Res -> return context.getString(snackBarMessage)
        else -> ""
    }
}


fun ErrorHolder.getErrorState(): BaseState =
        when (this) {
            is ErrorHolder.UnAuthorized ->
                BaseState.unAuthorized(this.errorMessage)
            is ErrorHolder.Message ->
                BaseState.showError(message)
            is ErrorHolder.StringRes ->
                BaseState.showError(stringID)
        }
