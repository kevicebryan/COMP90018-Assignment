package com.example.mobilecomputingassignment.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.data.utils.TeamLogoMapper
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.models.NoiseLevel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Utility class to generate custom marker icons for map pins Shows team logos for AFL events and
 * league logos for other competitions
 */
object CustomMarkerIconGenerator {

    private const val PIN_WIDTH_DP = 96 // Increased from 72 to 96 for better text fit
    private const val PIN_HEIGHT_DP = 56
    private const val TRIANGLE_HEIGHT_DP = 12
    private const val LOGO_SIZE_DP = 30
    private const val CORNER_RADIUS_DP = 16
    private const val SHADOW_OFFSET_DP = 2
    private const val SHADOW_BLUR_DP = 4
    private const val VENUE_TEXT_SIZE_DP = 11 // Slightly increased for better readability
    private const val NOISE_ICON_SIZE_DP = 12
    private const val NOISE_CIRCLE_SIZE_DP = 10 // Slightly larger for better visibility

    /** Check if the match is happening today */
    private fun isMatchToday(event: Event): Boolean {
        val matchDate = event.matchDetails?.matchTime ?: event.date
        val today = java.util.Calendar.getInstance()
        val matchCalendar = java.util.Calendar.getInstance()
        matchCalendar.time = matchDate

        return today.get(java.util.Calendar.YEAR) == matchCalendar.get(java.util.Calendar.YEAR) &&
                today.get(java.util.Calendar.DAY_OF_YEAR) ==
                        matchCalendar.get(java.util.Calendar.DAY_OF_YEAR)
    }

    /**
     * Generate a custom marker icon for an event
     * @param context Android context
     * @param event The event to generate marker for
     * @return BitmapDescriptor for the custom marker
     */
    fun generateMarkerIcon(context: Context, event: Event): BitmapDescriptor {
        val matchDetails = event.matchDetails

        return if (matchDetails != null) {
            // Determine if this is an AFL event (show team logos)
            // For F1 and other leagues, show league logo
            if (matchDetails.competition.equals("AFL", ignoreCase = true)) {
                generateAFLTeamMarker(context, event)
            } else {
                generateLeagueMarker(context, event)
            }
        } else {
            // Default marker for events without match details
            generateDefaultMarker(context, event)
        }
    }

    /** Generate marker with AFL team logos */
    private fun generateAFLTeamMarker(context: Context, event: Event): BitmapDescriptor {
        val matchDetails = event.matchDetails!!
        val homeTeamLogo = TeamLogoMapper.getTeamLogoByName(matchDetails.homeTeam)
        val awayTeamLogo = TeamLogoMapper.getTeamLogoByName(matchDetails.awayTeam)

        return if (homeTeamLogo != null && awayTeamLogo != null) {
            // Create a marker with both team logos side by side
            generateDualTeamMarker(context, homeTeamLogo, awayTeamLogo, event)
        } else {
            // Fallback to league logo if team logos not found
            generateLeagueMarker(context, event)
        }
    }

    /** Generate marker with league logo */
    private fun generateLeagueMarker(context: Context, event: Event): BitmapDescriptor {
        val competition = event.matchDetails?.competition ?: ""
        val leagueLogoRes =
                when (competition.uppercase()) {
                    "AFL" -> R.drawable.league_afl
                    "F1" -> R.drawable.league_f1
                    "A-LEAGUE" -> R.drawable.league_a_league
                    "PREMIER LEAGUE" -> R.drawable.league_premier_league
                    "NBA" -> R.drawable.league_nba
                    else -> R.drawable.ic_league // Default league icon
                }

        return generateSingleLogoMarker(context, leagueLogoRes, event)
    }

    /** Generate default marker for events without match details */
    private fun generateDefaultMarker(context: Context, event: Event): BitmapDescriptor {
        return generateSingleLogoMarker(context, R.drawable.ic_sports, event)
    }

    /** Generate a marker with a single logo */
    private fun generateSingleLogoMarker(
            context: Context,
            logoRes: Int,
            event: Event
    ): BitmapDescriptor {
        val density = context.resources.displayMetrics.density
        val pinWidth = (PIN_WIDTH_DP * density).toInt()
        val pinHeight = (PIN_HEIGHT_DP * density).toInt()
        val triangleHeight = (TRIANGLE_HEIGHT_DP * density).toInt()
        val logoSize = (LOGO_SIZE_DP * density).toInt()
        val cornerRadius = (CORNER_RADIUS_DP * density).toInt()
        val shadowOffset = (SHADOW_OFFSET_DP * density).toInt()
        val shadowBlur = (SHADOW_BLUR_DP * density).toInt()
        val venueTextSize = (VENUE_TEXT_SIZE_DP * density).toInt()
        val noiseIconSize = (NOISE_ICON_SIZE_DP * density).toInt()
        val noiseCircleSize = (NOISE_CIRCLE_SIZE_DP * density).toInt()

        // Add extra space for shadow only (venue text is now inside the rectangle)
        val totalHeight = pinHeight + triangleHeight + shadowOffset + shadowBlur
        val totalWidth = pinWidth + shadowOffset + shadowBlur

        // Create bitmap for the marker with shadow space
        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw shadow first
        paint.setShadowLayer(
                shadowBlur.toFloat(),
                shadowOffset.toFloat(),
                shadowOffset.toFloat(),
                Color.argb(80, 0, 0, 0)
        )

        // Determine marker background color based on match date and noise level
        val isToday = isMatchToday(event)
        val noiseLevel = NoiseLevel.fromDbfs(event.recentNoiseDbfs)

        // Set background color: orange for today's matches, white for others
        val backgroundColor =
                if (isToday) {
                    Color.parseColor("#FF6B35") // WatchMates primary orange color
                } else {
                    Color.WHITE
                }

        paint.color = backgroundColor
        val rect = RectF(0f, 0f, pinWidth.toFloat(), pinHeight.toFloat())
        canvas.drawRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)

        // Draw triangle pointing downward at bottom center
        val trianglePath = android.graphics.Path()
        val triangleCenterX = pinWidth / 2f
        val triangleTopY = pinHeight.toFloat()
        val triangleBottomY = pinHeight + triangleHeight.toFloat()
        val triangleWidth = triangleHeight.toFloat() // Make triangle equilateral

        trianglePath.moveTo(triangleCenterX, triangleBottomY) // Start from bottom point
        trianglePath.lineTo(triangleCenterX - triangleWidth / 2, triangleTopY) // Left point
        trianglePath.lineTo(triangleCenterX + triangleWidth / 2, triangleTopY) // Right point
        trianglePath.close()

        canvas.drawPath(trianglePath, paint)

        // Clear shadow for logo and text
        paint.clearShadowLayer()

        // Draw venue name at the top inside the rectangle
        if (event.location.name.isNotEmpty()) {
            paint.color = Color.BLACK
            paint.textSize = venueTextSize.toFloat()
            paint.textAlign = Paint.Align.CENTER

            val textY = venueTextSize.toFloat() + 6 // Slightly more padding from top
            val textX = pinWidth / 2f

            // Allow longer text with the wider marker (increased from 15 to 20 characters)
            val venueName =
                    if (event.location.name.length > 20) {
                        event.location.name.substring(0, 17) + "..."
                    } else {
                        event.location.name
                    }

            canvas.drawText(venueName, textX, textY, paint)
        }

        // Draw logo in center of rounded rectangle (below venue text)
        val logoDrawable = ContextCompat.getDrawable(context, logoRes)
        if (logoDrawable != null) {
            val logoLeft = (pinWidth - logoSize) / 2
            val logoTop =
                    (pinHeight - logoSize) / 2 +
                            (if (event.location.name.isNotEmpty()) venueTextSize / 2 else 0)
            val logoRight = logoLeft + logoSize
            val logoBottom = logoTop + logoSize

            // Save canvas state
            canvas.save()

            // Calculate scaling to maintain 1:1 aspect ratio and fit within bounds
            val intrinsicWidth = logoDrawable.intrinsicWidth
            val intrinsicHeight = logoDrawable.intrinsicHeight

            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                val scaleX = logoSize.toFloat() / intrinsicWidth
                val scaleY = logoSize.toFloat() / intrinsicHeight
                val scale = minOf(scaleX, scaleY) // Use the smaller scale to fit-contain

                // Calculate centered position
                val centerX = (logoLeft + logoRight) / 2f
                val centerY = (logoTop + logoBottom) / 2f

                // Apply scaling and centering
                canvas.translate(centerX, centerY)
                canvas.scale(scale, scale)

                // Set bounds to the intrinsic size since we're scaling
                logoDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                canvas.translate(-intrinsicWidth / 2f, -intrinsicHeight / 2f)
            } else {
                // Fallback: use original bounds if intrinsic size is not available
                logoDrawable.setBounds(logoLeft, logoTop, logoRight, logoBottom)
            }

            logoDrawable.draw(canvas)

            // Restore canvas state
            canvas.restore()
        }

        // Draw noise level indicator if available and event is active
        if (noiseLevel != null && event.isActive) {
            // Draw noise level circle centered below the logo
            val circleX = pinWidth / 2f // Center horizontally
            val circleY = pinHeight - noiseCircleSize - 8f // Position below logo with padding
            val circleRadius = noiseCircleSize / 2f

            paint.color = noiseLevel.color
            canvas.drawCircle(circleX, circleY, circleRadius, paint)

            // Draw volume icon in the circle
            val volumeIcon = ContextCompat.getDrawable(context, R.drawable.ic_volume)
            if (volumeIcon != null) {
                val iconSize = (noiseIconSize * 0.6f).toInt() // Slightly smaller for better fit
                val iconLeft = (circleX - iconSize / 2).toInt()
                val iconTop = (circleY - iconSize / 2).toInt()
                val iconRight = iconLeft + iconSize
                val iconBottom = iconTop + iconSize

                volumeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                volumeIcon.setTint(Color.WHITE)
                volumeIcon.draw(canvas)
            }
        }

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /** Generate a marker with two team logos side by side */
    private fun generateDualTeamMarker(
            context: Context,
            homeTeamLogo: Int,
            awayTeamLogo: Int,
            event: Event
    ): BitmapDescriptor {
        val density = context.resources.displayMetrics.density
        val pinWidth = (PIN_WIDTH_DP * density).toInt()
        val pinHeight = (PIN_HEIGHT_DP * density).toInt()
        val triangleHeight = (TRIANGLE_HEIGHT_DP * density).toInt()
        val logoSize = (LOGO_SIZE_DP * density).toInt()
        val cornerRadius = (CORNER_RADIUS_DP * density).toInt()
        val shadowOffset = (SHADOW_OFFSET_DP * density).toInt()
        val shadowBlur = (SHADOW_BLUR_DP * density).toInt()
        val venueTextSize = (VENUE_TEXT_SIZE_DP * density).toInt()
        val noiseIconSize = (NOISE_ICON_SIZE_DP * density).toInt()
        val noiseCircleSize = (NOISE_CIRCLE_SIZE_DP * density).toInt()

        // Add extra space for shadow only (venue text is now inside the rectangle)
        val totalHeight = pinHeight + triangleHeight + shadowOffset + shadowBlur
        val totalWidth = pinWidth + shadowOffset + shadowBlur

        // Create bitmap for the marker with shadow space
        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw shadow first
        paint.setShadowLayer(
                shadowBlur.toFloat(),
                shadowOffset.toFloat(),
                shadowOffset.toFloat(),
                Color.argb(80, 0, 0, 0)
        )

        // Determine marker background color based on match date and noise level
        val isToday = isMatchToday(event)
        val noiseLevel = NoiseLevel.fromDbfs(event.recentNoiseDbfs)

        // Set background color: orange for today's matches, white for others
        val backgroundColor =
                if (isToday) {
                    Color.parseColor("#FF6B35") // WatchMates primary orange color
                } else {
                    Color.WHITE
                }

        paint.color = backgroundColor
        val rect = RectF(0f, 0f, pinWidth.toFloat(), pinHeight.toFloat())
        canvas.drawRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)

        // Draw triangle pointing downward at bottom center
        val trianglePath = android.graphics.Path()
        val triangleCenterX = pinWidth / 2f
        val triangleTopY = pinHeight.toFloat()
        val triangleBottomY = pinHeight + triangleHeight.toFloat()
        val triangleWidth = triangleHeight.toFloat() // Make triangle equilateral

        trianglePath.moveTo(triangleCenterX, triangleBottomY) // Start from bottom point
        trianglePath.lineTo(triangleCenterX - triangleWidth / 2, triangleTopY) // Left point
        trianglePath.lineTo(triangleCenterX + triangleWidth / 2, triangleTopY) // Right point
        trianglePath.close()

        canvas.drawPath(trianglePath, paint)

        // Clear shadow for logos and text
        paint.clearShadowLayer()

        // Draw venue name at the top inside the rectangle
        if (event.location.name.isNotEmpty()) {
            paint.color = Color.BLACK
            paint.textSize = venueTextSize.toFloat()
            paint.textAlign = Paint.Align.CENTER

            val textY = venueTextSize.toFloat() + 6 // Slightly more padding from top
            val textX = pinWidth / 2f

            // Allow longer text with the wider marker (increased from 15 to 20 characters)
            val venueName =
                    if (event.location.name.length > 20) {
                        event.location.name.substring(0, 17) + "..."
                    } else {
                        event.location.name
                    }

            canvas.drawText(venueName, textX, textY, paint)
        }

        // Draw home team logo on the left (below venue text)
        val homeLogoDrawable = ContextCompat.getDrawable(context, homeTeamLogo)
        if (homeLogoDrawable != null) {
            val logoSpacing = 8 * density // 8dp spacing between logos for better horizontal padding
            val totalLogoWidth = (logoSize * 2) + logoSpacing
            val startX = (pinWidth - totalLogoWidth) / 2

            val homeLogoLeft = startX.toInt()
            val logoTop =
                    (pinHeight - logoSize) / 2 +
                            (if (event.location.name.isNotEmpty()) venueTextSize / 2 else 0)
            val homeLogoRight = homeLogoLeft + logoSize
            val logoBottom = logoTop + logoSize

            // Save canvas state
            canvas.save()

            // Calculate scaling to maintain 1:1 aspect ratio and fit within bounds
            val intrinsicWidth = homeLogoDrawable.intrinsicWidth
            val intrinsicHeight = homeLogoDrawable.intrinsicHeight

            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                val scaleX = logoSize.toFloat() / intrinsicWidth
                val scaleY = logoSize.toFloat() / intrinsicHeight
                val scale = minOf(scaleX, scaleY) // Use the smaller scale to fit-contain

                // Calculate centered position
                val centerX = (homeLogoLeft + homeLogoRight) / 2f
                val centerY = (logoTop + logoBottom) / 2f

                // Apply scaling and centering
                canvas.translate(centerX, centerY)
                canvas.scale(scale, scale)

                // Set bounds to the intrinsic size since we're scaling
                homeLogoDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                canvas.translate(-intrinsicWidth / 2f, -intrinsicHeight / 2f)
            } else {
                // Fallback: use original bounds if intrinsic size is not available
                homeLogoDrawable.setBounds(homeLogoLeft, logoTop, homeLogoRight, logoBottom)
            }

            homeLogoDrawable.draw(canvas)

            // Restore canvas state
            canvas.restore()
        }

        // Draw away team logo on the right (below venue text)
        val awayLogoDrawable = ContextCompat.getDrawable(context, awayTeamLogo)
        if (awayLogoDrawable != null) {
            val logoSpacing = 8 * density // 8dp spacing between logos for better horizontal padding
            val totalLogoWidth = (logoSize * 2) + logoSpacing
            val startX = (pinWidth - totalLogoWidth) / 2

            val awayLogoLeft = (startX + logoSize + logoSpacing).toInt()
            val logoTop =
                    (pinHeight - logoSize) / 2 +
                            (if (event.location.name.isNotEmpty()) venueTextSize / 2 else 0)
            val awayLogoRight = awayLogoLeft + logoSize
            val logoBottom = logoTop + logoSize

            // Save canvas state
            canvas.save()

            // Calculate scaling to maintain 1:1 aspect ratio and fit within bounds
            val intrinsicWidth = awayLogoDrawable.intrinsicWidth
            val intrinsicHeight = awayLogoDrawable.intrinsicHeight

            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                val scaleX = logoSize.toFloat() / intrinsicWidth
                val scaleY = logoSize.toFloat() / intrinsicHeight
                val scale = minOf(scaleX, scaleY) // Use the smaller scale to fit-contain

                // Calculate centered position
                val centerX = (awayLogoLeft + awayLogoRight) / 2f
                val centerY = (logoTop + logoBottom) / 2f

                // Apply scaling and centering
                canvas.translate(centerX, centerY)
                canvas.scale(scale, scale)

                // Set bounds to the intrinsic size since we're scaling
                awayLogoDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                canvas.translate(-intrinsicWidth / 2f, -intrinsicHeight / 2f)
            } else {
                // Fallback: use original bounds if intrinsic size is not available
                awayLogoDrawable.setBounds(awayLogoLeft, logoTop, awayLogoRight, logoBottom)
            }

            awayLogoDrawable.draw(canvas)

            // Restore canvas state
            canvas.restore()
        }

        // Draw noise level indicator if available and event is active
        if (noiseLevel != null && event.isActive) {
            // Draw noise level circle centered below the logos
            val circleX = pinWidth / 2f // Center horizontally
            val circleY = pinHeight - noiseCircleSize - 8f // Position below logos with padding
            val circleRadius = noiseCircleSize / 2f

            paint.color = noiseLevel.color
            canvas.drawCircle(circleX, circleY, circleRadius, paint)

            // Draw volume icon in the circle
            val volumeIcon = ContextCompat.getDrawable(context, R.drawable.ic_volume)
            if (volumeIcon != null) {
                val iconSize = (noiseIconSize * 0.6f).toInt() // Slightly smaller for better fit
                val iconLeft = (circleX - iconSize / 2).toInt()
                val iconTop = (circleY - iconSize / 2).toInt()
                val iconRight = iconLeft + iconSize
                val iconBottom = iconTop + iconSize

                volumeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                volumeIcon.setTint(Color.WHITE)
                volumeIcon.draw(canvas)
            }
        }

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
