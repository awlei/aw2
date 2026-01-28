package com.v2ray.ang.util

import android.util.Log
import com.v2ray.ang.AppConfig
import java.util.regex.Pattern

/**
 * Security utility for validating inputs, sanitizing data, and enhancing application security.
 *
 * This utility provides methods to validate user inputs, detect potential security threats,
 * and ensure data integrity throughout the application.
 *
 * @author V2rayNG Team
 * @since 2.0.6
 */
object SecurityUtils {

    private const val TAG = "${AppConfig.TAG}.Security"

    // SQL Injection patterns
    private val SQL_INJECTION_PATTERNS = listOf(
        Pattern.compile("(?i)(\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE)?|INSERT( +INTO)?|MERGE|SELECT|UPDATE|UNION( +ALL)?|HAVING|GROUP +BY)\\b)"),
        Pattern.compile("(?i)(--|;|\\/\\*|\\*/|@@|xp_|sp_)"),
        Pattern.compile("(?i)(''|'\\s*(OR|AND)\\s*'|'\\s*=\\s*'|'\\s*!=\\s*')")
    )

    // XSS patterns
    private val XSS_PATTERNS = listOf(
        Pattern.compile("(?i)<script[^>]*>.*?</script>"),
        Pattern.compile("(?i)javascript:"),
        Pattern.compile("(?i)on\\w+\\s*="),
        Pattern.compile("(?i)<iframe[^>]*>.*?</iframe>"),
        Pattern.compile("(?i)<object[^>]*>.*?</object>"),
        Pattern.compile("(?i)<embed[^>]*>.*?</embed>")
    )

    // Path traversal patterns
    private val PATH_TRAVERSAL_PATTERNS = listOf(
        Pattern.compile("\\.\\.[/\\\\]"),
        Pattern.compile("%2e%2e[/\\\\]"),
        Pattern.compile("%252e%252e[/\\\\]")
    )

    /**
     * Sanitizes a string input to prevent common attacks.
     *
     * @param input The input string to sanitize
     * @return A sanitized version of the input
     */
    fun sanitizeInput(input: String?): String {
        if (input.isNullOrBlank()) return ""

        return input.trim()
            .replace(Regex("<[^>]+>"), "") // Remove HTML tags
            .replace(Regex("[<>\"']"), "") // Remove dangerous characters
            .take(1000) // Limit length
    }

    /**
     * Validates that an input is safe from SQL injection attacks.
     *
     * @param input The input to validate
     * @return True if the input is safe, false otherwise
     */
    fun isSqlInjectionSafe(input: String?): Boolean {
        if (input.isNullOrBlank()) return true

        return SQL_INJECTION_PATTERNS.none { it.matcher(input).find() }
    }

    /**
     * Validates that an input is safe from XSS attacks.
     *
     * @param input The input to validate
     * @return True if the input is safe, false otherwise
     */
    fun isXssSafe(input: String?): Boolean {
        if (input.isNullOrBlank()) return true

        return XSS_PATTERNS.none { it.matcher(input).find() }
    }

    /**
     * Validates that a path is safe from directory traversal attacks.
     *
     * @param path The path to validate
     * @return True if the path is safe, false otherwise
     */
    fun isPathSafe(path: String?): Boolean {
        if (path.isNullOrBlank()) return true

        return PATH_TRAVERSAL_PATTERNS.none { it.matcher(path).find() }
    }

    /**
     * Validates a URL to ensure it's safe and well-formed.
     *
     * @param url The URL to validate
     * @param allowHttp Whether to allow HTTP (less secure) URLs (default: false)
     * @return True if the URL is safe, false otherwise
     */
    fun isUrlSafe(url: String?, allowHttp: Boolean = false): Boolean {
        if (url.isNullOrBlank()) return false

        try {
            val urlObj = java.net.URL(url)

            // Check protocol
            val protocol = urlObj.protocol.lowercase()
            if (protocol == "http" && !allowHttp) {
                Log.w(TAG, "HTTP URL not allowed: $url")
                return false
            }

            if (protocol !in listOf("https", "http")) {
                Log.w(TAG, "Invalid protocol: $protocol")
                return false
            }

            // Check for suspicious characters
            if (!isXssSafe(url)) {
                Log.w(TAG, "XSS detected in URL")
                return false
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Invalid URL format: $url", e)
            return false
        }
    }

    /**
     * Validates a port number.
     *
     * @param port The port number to validate
     * @param privilegedAllowed Whether to allow privileged ports (1-1023) (default: false)
     * @return True if the port is valid, false otherwise
     */
    fun isPortValid(port: Int?, privilegedAllowed: Boolean = false): Boolean {
        if (port == null) return false

        return when {
            port < 1 -> false
            port > 65535 -> false
            port <= 1023 && !privilegedAllowed -> false
            else -> true
        }
    }

    /**
     * Masks sensitive information for logging purposes.
     *
     * @param data The sensitive data (e.g., password, token)
     * @param visibleChars Number of characters to keep visible (default: 4)
     * @return The masked string
     */
    fun maskSensitiveData(data: String?, visibleChars: Int = 4): String {
        if (data.isNullOrBlank()) return ""

        val prefix = if (data.length > visibleChars) data.take(visibleChars) else ""
        val maskedPart = "*".repeat(coerceAtLeast(data.length - visibleChars, 0))

        return "$prefix$maskedPart"
    }

    /**
     * Validates a UUID format.
     *
     * @param uuid The UUID string to validate
     * @return True if the UUID is valid, false otherwise
     */
    fun isValidUuid(uuid: String?): Boolean {
        if (uuid.isNullOrBlank()) return false

        val uuidPattern = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )

        return uuidPattern.matcher(uuid).matches()
    }

    /**
     * Checks if a string contains only safe ASCII characters.
     *
     * @param input The input to check
     * @return True if the string contains only safe ASCII, false otherwise
     */
    fun isSafeAscii(input: String?): Boolean {
        if (input.isNullOrBlank()) return true

        return input.all { it.code in 32..126 || it == '\n' || it == '\r' || it == '\t' }
    }

    /**
     * Validates a domain name.
     *
     * @param domain The domain to validate
     * @return True if the domain is valid, false otherwise
     */
    fun isDomainValid(domain: String?): Boolean {
        if (domain.isNullOrBlank()) return false

        // Basic domain validation
        val domainPattern = Pattern.compile(
            "^(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.[A-Za-z0-9-]{1,63})*$"
        )

        if (!domainPattern.matcher(domain).matches()) return false

        // Length check
        if (domain.length > 253) return false

        // Check for path traversal
        if (!isPathSafe(domain)) return false

        return true
    }

    /**
     * Securely compares two strings in constant time to prevent timing attacks.
     *
     * @param a First string
     * @param b Second string
     * @return True if strings are equal, false otherwise
     */
    fun constantTimeEquals(a: String?, b: String?): Boolean {
        if (a == null || b == null) return a == b

        if (a.length != b.length) return false

        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }

        return result == 0
    }
}

/**
 * Extension function to safely convert a string to an integer with bounds checking.
 *
 * @param default The default value if conversion fails
 * @param min Minimum allowed value
 * @param max Maximum allowed value
 * @return The parsed and bounded integer, or default if conversion fails
 */
fun String?.toIntSafe(default: Int = 0, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int {
    return this.toIntOrNull()?.coerceIn(min, max) ?: default
}

/**
 * Extension function to safely convert a string to a long with bounds checking.
 *
 * @param default The default value if conversion fails
 * @param min Minimum allowed value
 * @param max Maximum allowed value
 * @return The parsed and bounded long, or default if conversion fails
 */
fun String?.toLongSafe(default: Long = 0L, min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE): Long {
    return this.toLongOrNull()?.coerceIn(min, max) ?: default
}
