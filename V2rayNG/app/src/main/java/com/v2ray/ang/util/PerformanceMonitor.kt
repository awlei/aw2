package com.v2ray.ang.util

import android.util.Log
import com.v2ray.ang.AppConfig

/**
 * Performance monitoring and optimization utility.
 *
 * This utility provides tools for tracking performance metrics,
 * monitoring memory usage, and identifying performance bottlenecks.
 *
 * @author V2rayNG Team
 * @since 2.0.6
 */
object PerformanceMonitor {

    private const val TAG = "${AppConfig.TAG}.PerfMonitor"
    private val timers = mutableMapOf<String, Long>()

    /**
     * Starts a performance timer for a given operation.
     *
     * @param name The name of the operation to time
     */
    fun startTimer(name: String) {
        timers[name] = System.nanoTime()
    }

    /**
     * Stops a performance timer and logs the elapsed time.
     *
     * @param name The name of the operation
     * @param logThresholdMs The threshold in milliseconds above which to log (default: 100ms)
     */
    fun stopTimer(name: String, logThresholdMs: Long = 100) {
        val startTime = timers.remove(name) ?: return
        val elapsedNanos = System.nanoTime() - startTime
        val elapsedMs = elapsedNanos / 1_000_000

        if (elapsedMs >= logThresholdMs) {
            Log.w(TAG, "Performance: $name took ${elapsedMs}ms")
        }
    }

    /**
     * Measures and logs the execution time of a code block.
     *
     * @param name The name of the operation
     * @param logThresholdMs The threshold in milliseconds above which to log
     * @param block The code block to measure
     * @return The result of the code block
     */
    inline fun <T> measureTime(
        name: String,
        logThresholdMs: Long = 100,
        block: () -> T
    ): T {
        val startTime = System.nanoTime()
        val result = block()
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        if (elapsedMs >= logThresholdMs) {
            Log.w(TAG, "Performance: $name took ${elapsedMs}ms")
        }

        return result
    }

    /**
     * Logs current memory usage statistics.
     *
     * @param context Optional context description
     */
    fun logMemoryUsage(context: String = "") {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        val freeMemory = runtime.freeMemory() / (1024 * 1024)

        val contextPrefix = if (context.isNotEmpty()) "[$context] " else ""
        Log.d(
            TAG,
            "${contextPrefix}Memory: Used=${usedMemory}MB, Free=${freeMemory}MB, Max=${maxMemory}MB"
        )
    }

    /**
     * Suggests garbage collection (system will decide if it's appropriate).
     *
     * Note: This should be used sparingly and only in specific scenarios.
     */
    fun suggestGarbageCollection() {
        System.gc()
        Log.d(TAG, "Garbage collection suggested")
    }
}

/**
 * Memory cache implementation with automatic cleanup.
 *
 * This cache stores objects and provides automatic cleanup
 * of old or unused entries to prevent memory leaks.
 *
 * @param maxSize Maximum number of entries to keep
 * @author V2rayNG Team
 * @since 2.0.6
 */
class MemoryCache<K, V>(
    private val maxSize: Int = 100
) {
    private val cache = LinkedHashMap<K, V>(maxSize, 0.75f, true)
    private val accessTimes = mutableMapOf<K, Long>()

    /**
     * Gets a value from the cache.
     *
     * @param key The key to look up
     * @return The cached value, or null if not found
     */
    fun get(key: K): V? {
        val value = cache[key]
        if (value != null) {
            accessTimes[key] = System.currentTimeMillis()
        }
        return value
    }

    /**
     * Puts a value into the cache.
     *
     * @param key The key to store
     * @param value The value to cache
     */
    fun put(key: K, value: V) {
        // Remove oldest entry if cache is full
        if (cache.size >= maxSize && !cache.containsKey(key)) {
            val oldestKey = accessTimes.minByOrNull { it.value }?.key
            oldestKey?.let {
                cache.remove(it)
                accessTimes.remove(it)
            }
        }

        cache[key] = value
        accessTimes[key] = System.currentTimeMillis()
    }

    /**
     * Removes a value from the cache.
     *
     * @param key The key to remove
     * @return The removed value, or null if not found
     */
    fun remove(key: K): V? {
        accessTimes.remove(key)
        return cache.remove(key)
    }

    /**
     * Clears the entire cache.
     */
    fun clear() {
        cache.clear()
        accessTimes.clear()
    }

    /**
     * Returns the current size of the cache.
     *
     * @return Number of cached entries
     */
    fun size(): Int = cache.size

    /**
     * Removes entries that haven't been accessed for a given time period.
     *
     * @param maxAgeMs Maximum age in milliseconds
     * @return Number of entries removed
     */
    fun removeOldEntries(maxAgeMs: Long): Int {
        val now = System.currentTimeMillis()
        val keysToRemove = accessTimes.filter { now - it.value > maxAgeMs }.keys

        keysToRemove.forEach {
            cache.remove(it)
            accessTimes.remove(it)
        }

        return keysToRemove.size
    }
}
