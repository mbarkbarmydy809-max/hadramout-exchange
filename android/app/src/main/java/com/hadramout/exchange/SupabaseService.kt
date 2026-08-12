package com.hadramout.exchange

import java.net.HttpURLConnection
import java.net.URL

/**
 * Small dependency-free REST boundary for the Supabase project.
 *
 * The first APK intentionally ships with demo placeholders because the
 * Supabase project values were not provided. Replace the two BuildConfig
 * fields in app/build.gradle.kts with secure build-time values before launch.
 */
object SupabaseService {
    private const val REST_PATH = "/rest/v1"

    val isDemoMode: Boolean
        get() = BuildConfig.SUPABASE_URL.contains("placeholder") ||
            BuildConfig.SUPABASE_ANON_KEY.contains("demo-anon-key")

    fun request(path: String, method: String = "GET"): String? {
        if (isDemoMode) return null

        val connection = (URL(BuildConfig.SUPABASE_URL + REST_PATH + path)
            .openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 8_000
            readTimeout = 8_000
            setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
            setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            setRequestProperty("Accept", "application/json")
        }

        return try {
            if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } finally {
            connection.disconnect()
        }
    }
}