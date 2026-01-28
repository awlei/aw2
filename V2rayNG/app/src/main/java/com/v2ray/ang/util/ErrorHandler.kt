package com.v2ray.ang.util

import android.util.Log
import com.v2ray.ang.AppConfig
import kotlinx.coroutines.CancellationException

/**
 * Enhanced error handling utility for consistent error management across the application.
 *
 * This utility provides centralized error logging, handling, and recovery mechanisms.
 * It helps maintain consistency in error reporting and improves debugging capabilities.
 *
 * @author V2rayNG Team
 * @since 2.0.6
 */
object ErrorHandler {

    private const val TAG = "${AppConfig.TAG}.ErrorHandler"
    private val errorCallbacks = mutableMapOf<String, (Exception) -> Unit>()

    /**
     * Safely executes a block of code with comprehensive error handling.
     *
     * @param block The code block to execute
     * @param onError Optional callback for error handling
     * @param context Optional context description for better logging
     * @return True if execution succeeded, false otherwise
     */
    inline fun <T> safeExecute(
        context: String = "",
        onError: ((Exception) -> Unit)? = null,
        block: () -> T
    ): T? {
        return try {
            block()
        } catch (e: CancellationException) {
            // Don't log cancellation exceptions as they are normal coroutine behavior
            throw e
        } catch (e: Exception) {
            logError(e, context)
            onError?.invoke(e)
            null
        }
    }

    /**
     * Safely executes a block of code with a default value on error.
     *
     * @param defaultValue The value to return on error
     * @param context Optional context description for better logging
     * @param block The code block to execute
     * @return The result of block or defaultValue on error
     */
    inline fun <T> safeExecuteOrDefault(
        defaultValue: T,
        context: String = "",
        block: () -> T
    ): T {
        return try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logError(e, context)
            defaultValue
        }
    }

    /**
     * Logs an error with context information.
     *
     * @param error The exception to log
     * @param context Optional context description
     * @param includeStackTrace Whether to include full stack trace (default: true)
     */
    fun logError(error: Exception, context: String = "", includeStackTrace: Boolean = true) {
        val contextPrefix = if (context.isNotEmpty()) "[$context] " else ""
        Log.e(TAG, "${contextPrefix}Error: ${error.message}")

        if (includeStackTrace) {
            Log.e(TAG, "${contextPrefix}Stack trace:", error)
        }
    }

    /**
     * Logs a warning message.
     *
     * @param message The warning message
     * @param context Optional context description
     */
    fun logWarning(message: String, context: String = "") {
        val contextPrefix = if (context.isNotEmpty()) "[$context] " else ""
        Log.w(TAG, "${contextPrefix}Warning: $message")
    }

    /**
     * Logs an informational message.
     *
     * @param message The info message
     * @param context Optional context description
     */
    fun logInfo(message: String, context: String = "") {
        val contextPrefix = if (context.isNotEmpty()) "[$context] " else ""
        Log.i(TAG, "${contextPrefix}Info: $message")
    }

    /**
     * Registers a callback for specific error types.
     *
     * @param key Unique identifier for the callback
     * @param callback The callback to invoke on error
     */
    fun registerErrorCallback(key: String, callback: (Exception) -> Unit) {
        errorCallbacks[key] = callback
    }

    /**
     * Unregisters an error callback.
     *
     * @param key The identifier of the callback to remove
     */
    fun unregisterErrorCallback(key: String) {
        errorCallbacks.remove(key)
    }

    /**
     * Invokes registered error callbacks.
     *
     * @param error The exception to pass to callbacks
     */
    private fun invokeErrorCallbacks(error: Exception) {
        errorCallbacks.values.forEach { callback ->
            try {
                callback(error)
            } catch (e: Exception) {
                Log.e(TAG, "Error in error callback", e)
            }
        }
    }

    /**
     * Checks if an exception is recoverable (should retry) or fatal (should fail).
     *
     * @param exception The exception to check
     * @return True if the exception is recoverable
     */
    fun isRecoverable(exception: Exception): Boolean {
        return when (exception) {
            is java.net.SocketTimeoutException,
            is java.net.UnknownHostException,
            is java.io.InterruptedIOException -> true
            else -> false
        }
    }

    /**
     * Gets a user-friendly error message for display.
     *
     * @param exception The exception
     * @return A user-friendly error message
     */
    fun getUserFriendlyMessage(exception: Exception): String {
        return when (exception) {
            is java.net.SocketTimeoutException -> "Connection timeout. Please check your network."
            is java.net.UnknownHostException -> "Unable to resolve host. Please check your connection."
            is java.net.ConnectException -> "Connection refused. The server may be down."
            is java.io.IOException -> "Network error. Please try again."
            else -> "An error occurred: ${exception.message}"
        }
    }
}
