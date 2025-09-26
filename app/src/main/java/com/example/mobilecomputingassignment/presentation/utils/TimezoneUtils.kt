package com.example.mobilecomputingassignment.presentation.utils

import java.util.*

/**
 * Timezone utility functions for handling Australian AFL events
 *
 * This utility class provides consistent timezone handling across the app, ensuring all AFL-related
 * dates and times are properly handled in Australian timezone.
 */
object TimezoneUtils {

  /**
   * Australian timezone (AEST/AEDT) used for AFL matches This automatically handles daylight saving
   * time transitions
   */
  val AUSTRALIAN_TIMEZONE: TimeZone = TimeZone.getTimeZone("Australia/Sydney")

  /** Get current date in Australian timezone */
  fun getCurrentAustralianDate(): Date {
    return Calendar.getInstance(AUSTRALIAN_TIMEZONE).time
  }

  /** Get today's date at midnight in Australian timezone */
  fun getTodayMidnightAustralian(): Date {
    return Calendar.getInstance(AUSTRALIAN_TIMEZONE)
            .apply {
              set(Calendar.HOUR_OF_DAY, 0)
              set(Calendar.MINUTE, 0)
              set(Calendar.SECOND, 0)
              set(Calendar.MILLISECOND, 0)
            }
            .time
  }

  /** Convert a date to Australian timezone and return date-only (midnight) */
  fun toAustralianDateOnly(date: Date): Date {
    return Calendar.getInstance(AUSTRALIAN_TIMEZONE)
            .apply {
              time = date
              set(Calendar.HOUR_OF_DAY, 0)
              set(Calendar.MINUTE, 0)
              set(Calendar.SECOND, 0)
              set(Calendar.MILLISECOND, 0)
            }
            .time
  }

  /** Check if a date is today or in the future in Australian timezone */
  fun isTodayOrFuture(date: Date): Boolean {
    val today = getTodayMidnightAustralian()
    val dateOnly = toAustralianDateOnly(date)
    return !dateOnly.before(today)
  }

  /** Calculate check-in time (30 minutes before match) in Australian timezone */
  fun calculateCheckInTime(matchTime: Date): Date {
    return Date(matchTime.time - (30 * 60 * 1000)) // 30 minutes before
  }

  /** Format date for display in Australian timezone */
  fun formatAustralianDate(date: Date): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    formatter.timeZone = AUSTRALIAN_TIMEZONE
    return formatter.format(date)
  }

  /** Format date for display preserving the original timezone */
  fun formatPreservedDate(date: Date): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
  }

  /** Format time for display in Australian timezone */
  fun formatAustralianTime(date: Date): String {
    val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
    formatter.timeZone = AUSTRALIAN_TIMEZONE
    return formatter.format(date)
  }

  /** Format time for display preserving the original timezone */
  fun formatPreservedTime(date: Date): String {
    val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
  }

  /** Format date and time for display in Australian timezone */
  fun formatAustralianDateTime(date: Date): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    formatter.timeZone = AUSTRALIAN_TIMEZONE
    return formatter.format(date)
  }

  /**
   * Create a Date object with specific date and time in Australian timezone
   * @param date The base date
   * @param hour Hour of day (0-23)
   * @param minute Minute of hour (0-59)
   * @return Date object in Australian timezone
   */
  fun createAustralianDateTime(date: Date, hour: Int, minute: Int): Date {
    val calendar = Calendar.getInstance(AUSTRALIAN_TIMEZONE)
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
  }

  /**
   * Create a Date object preserving the user's intended time regardless of device timezone
   * This ensures that if a user sets 16:30, it will always be 16:30 regardless of their location
   * @param date The base date
   * @param hour Hour of day (0-23) as intended by user
   * @param minute Minute of hour (0-59) as intended by user
   * @return Date object that preserves the user's intended time
   */
  fun createPreservedDateTime(date: Date, hour: Int, minute: Int): Date {
    // Use the same timezone as the input date to preserve the user's intended time
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
  }

  /**
   * Extract hour and minute from a Date object in Australian timezone
   * @param date The date to extract time from
   * @return Pair of (hour, minute) in Australian timezone
   */
  fun getAustralianHourMinute(date: Date): Pair<Int, Int> {
    val calendar = Calendar.getInstance(AUSTRALIAN_TIMEZONE)
    calendar.time = date
    return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
  }

  /**
   * Extract hour and minute from a Date object preserving the original timezone
   * @param date The date to extract time from
   * @return Pair of (hour, minute) in the date's original timezone
   */
  fun getPreservedHourMinute(date: Date): Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
  }
}
