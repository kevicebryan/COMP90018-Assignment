package com.example.mobilecomputingassignment.core.utils

import android.util.Log
import java.util.regex.Pattern

/**
 * ContentFilter - NSFW Content Detection Utility
 *
 * This utility class provides functions to detect and filter inappropriate content from user inputs
 * to maintain a safe and ethical environment.
 *
 * Key Features:
 * - Profanity detection using regex patterns
 * - Common inappropriate words and phrases
 * - Case-insensitive matching
 * - Configurable sensitivity levels
 *
 * Usage:
 * - Username validation during registration
 * - Venue name and address validation for events
 * - Any user-generated content that needs moderation
 */
object ContentFilter {

  private const val TAG = "ContentFilter"

  // Common inappropriate words and phrases (case-insensitive)
  private val INAPPROPRIATE_WORDS =
          setOf(
                  // Profanity
                  "fuck",
                  "shit",
                  "bitch",
                  "ass",
                  "damn",
                  "hell",
                  "crap",
                  "piss",
                  "dick",
                  "cock",
                  "pussy",
                  "cunt",
                  "whore",
                  "slut",
                  "bastard",
                  "motherfucker",
                  "fucker",

                  // Hate speech and discriminatory terms
                  "nazi",
                  "hitler",
                  "racist",
                  "sexist",
                  "homophobic",
                  "transphobic",
                  "bigot",
                  "hate",
                  "kill",
                  "murder",

                  // Inappropriate sexual content
                  "porn",
                  "sex",
                  "nude",
                  "naked",
                  "penis",
                  "vagina",
                  "boobs",
                  "tits",
                  "asshole",
                  "butthole",
                  "dildo",

                  // Violence and threats
                  "kill",
                  "murder",
                  "death",
                  "suicide",
                  "bomb",
                  "terrorist",
                  "shoot",
                  "gun",
                  "weapon",
                  "violence",
                  "attack",

                  // Drugs and illegal activities
                  "cocaine",
                  "heroin",
                  "meth",
                  "weed",
                  "drugs",
                  "dealer",
                  "illegal",
                  "crime",
                  "steal",
                  "rob",
                  "hack"
          )

  // Regex patterns for more complex inappropriate content
  private val INAPPROPRIATE_PATTERNS =
          listOf(
                  // URLs and links (often used for spam)
                  Pattern.compile("https?://", Pattern.CASE_INSENSITIVE),

                  // Phone numbers (privacy concern)
                  Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b"),

                  // Email addresses (privacy concern)
                  Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"),

                  // Excessive repetition (spam-like behavior)
                  Pattern.compile("(.)\\1{4,}"), // Same character repeated 5+ times

                  // All caps (often indicates shouting/aggression)
                  Pattern.compile("^[A-Z\\s]{10,}$"), // 10+ characters all caps

                  // Excessive punctuation
                  Pattern.compile(
                          "[!@#\\$%\\^&\\*\\(\\)\\-_=\\+\\[\\]\\{\\}\\|;:'\",.<>/?]{5,}"
                  ) // 5+ special chars
          )

  // Minimum length for content to be considered valid
  private const val MIN_LENGTH = 2

  // Maximum length to prevent abuse
  private const val MAX_LENGTH = 100

  /**
   * Checks if a string contains inappropriate content
   *
   * @param input The string to check
   * @return true if the content is inappropriate, false if it's safe
   */
  fun isInappropriate(input: String): Boolean {
    if (input.isBlank()) {
      return true // Empty content is not allowed
    }

    val trimmedInput = input.trim()

    // Check length constraints
    if (trimmedInput.length < MIN_LENGTH || trimmedInput.length > MAX_LENGTH) {
      Log.d(TAG, "Content length violation: ${trimmedInput.length} characters")
      return true
    }

    // Convert to lowercase for case-insensitive matching
    val lowerInput = trimmedInput.lowercase()

    // Check for inappropriate words
    val words = lowerInput.split("\\s+".toRegex())
    for (word in words) {
      val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "") // Remove special characters
      if (INAPPROPRIATE_WORDS.contains(cleanWord)) {
        Log.d(TAG, "Inappropriate word detected: $cleanWord")
        return true
      }
    }

    // Check for inappropriate patterns
    for (pattern in INAPPROPRIATE_PATTERNS) {
      if (pattern.matcher(trimmedInput).find()) {
        Log.d(TAG, "Inappropriate pattern detected: ${pattern.pattern()}")
        return true
      }
    }

    // Check for partial matches (e.g., "fck" instead of "fuck")
    for (inappropriateWord in INAPPROPRIATE_WORDS) {
      if (lowerInput.contains(inappropriateWord, ignoreCase = true) ||
                      inappropriateWord.contains(lowerInput, ignoreCase = true) ||
                      isPartialMatch(lowerInput, inappropriateWord)
      ) {
        Log.d(TAG, "Partial inappropriate match detected: $inappropriateWord")
        return true
      }
    }

    return false
  }

  /**
   * Checks if a string is safe for public display
   *
   * @param input The string to validate
   * @return ValidationResult with success status and error message if applicable
   */
  fun validateContent(input: String): ValidationResult {
    return when {
      input.isBlank() -> ValidationResult(false, "Content cannot be empty")
      input.length < MIN_LENGTH ->
              ValidationResult(false, "Content must be at least $MIN_LENGTH characters")
      input.length > MAX_LENGTH ->
              ValidationResult(false, "Content cannot exceed $MAX_LENGTH characters")
      isInappropriate(input) ->
              ValidationResult(false, "Content contains inappropriate language or patterns")
      else -> ValidationResult(true, null)
    }
  }

  /**
   * Sanitizes input by removing potentially harmful characters
   *
   * @param input The string to sanitize
   * @return Sanitized string safe for storage
   */
  fun sanitizeInput(input: String): String {
    return input.trim()
            .replace(Regex("[<>\"']"), "") // Remove HTML-like characters
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .take(MAX_LENGTH) // Limit length
  }

  /**
   * Checks for partial matches of inappropriate words (e.g., "fck" matches "fuck" with 75%
   * similarity)
   */
  private fun isPartialMatch(input: String, inappropriateWord: String): Boolean {
    if (input.length < 3 || inappropriateWord.length < 3) return false

    // Check if input is a significant portion of inappropriate word
    val similarity = calculateSimilarity(input, inappropriateWord)
    return similarity > 0.7 // 70% similarity threshold
  }

  /** Calculates similarity between two strings using Levenshtein distance */
  private fun calculateSimilarity(str1: String, str2: String): Double {
    val distance = levenshteinDistance(str1, str2)
    val maxLength = maxOf(str1.length, str2.length)
    return if (maxLength == 0) 1.0 else (maxLength - distance).toDouble() / maxLength
  }

  /** Calculates Levenshtein distance between two strings */
  private fun levenshteinDistance(str1: String, str2: String): Int {
    val matrix = Array(str1.length + 1) { IntArray(str2.length + 1) }

    for (i in 0..str1.length) matrix[i][0] = i
    for (j in 0..str2.length) matrix[0][j] = j

    for (i in 1..str1.length) {
      for (j in 1..str2.length) {
        val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
        matrix[i][j] =
                minOf(
                        matrix[i - 1][j] + 1, // deletion
                        matrix[i][j - 1] + 1, // insertion
                        matrix[i - 1][j - 1] + cost // substitution
                )
      }
    }

    return matrix[str1.length][str2.length]
  }
}

/** Result class for content validation */
data class ValidationResult(val isValid: Boolean, val errorMessage: String?)
