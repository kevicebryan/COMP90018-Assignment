package com.example.mobilecomputingassignment.domain.models

import android.graphics.Color

/**
 * Enum representing noise levels based on dBFS values Used for displaying noise level indicators on
 * map markers
 */
enum class NoiseLevel(
        val displayName: String,
        val color: Int,
        val minDbfs: Double,
        val maxDbfs: Double
) {
  QUIET("Quiet", Color.parseColor("#237D56"), -80.0, -40.0),
  MODERATE("Moderate", Color.parseColor("#C4BA00"), -40.0, -15.0),
  LOUD("Loud", Color.parseColor("#BF0000"), -15.0, Double.MAX_VALUE);

  companion object {
    /**
     * Get noise level from dBFS value
     * @param dbfs The dBFS value to evaluate
     * @return The corresponding NoiseLevel, or null if dbfs is null/NaN
     */
    fun fromDbfs(dbfs: Double?): NoiseLevel? {
      if (dbfs == null || dbfs.isNaN()) return null

      return values().find { dbfs >= it.minDbfs && dbfs < it.maxDbfs }
              ?: LOUD // Default to LOUD for values above our defined ranges
    }
  }
}
