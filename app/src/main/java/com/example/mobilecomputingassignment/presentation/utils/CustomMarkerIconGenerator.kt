package com.example.mobilecomputingassignment.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.data.constants.TeamConstants
import com.example.mobilecomputingassignment.data.utils.TeamLogoMapper
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.models.MatchDetails
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Utility class to generate custom marker icons for map pins
 * Shows team logos for AFL events and league logos for other competitions
 */
object CustomMarkerIconGenerator {
    
    private const val PIN_WIDTH_DP = 72
    private const val PIN_HEIGHT_DP = 56
    private const val TRIANGLE_HEIGHT_DP = 12
    private const val LOGO_SIZE_DP = 30
    private const val CORNER_RADIUS_DP = 16
    private const val SHADOW_OFFSET_DP = 2
    private const val SHADOW_BLUR_DP = 4
    
    /**
     * Generate a custom marker icon for an event
     * @param context Android context
     * @param event The event to generate marker for
     * @return BitmapDescriptor for the custom marker
     */
    fun generateMarkerIcon(context: Context, event: Event): BitmapDescriptor {
        val matchDetails = event.matchDetails
        
        return if (matchDetails != null) {
            // Determine if this is an AFL event
            if (matchDetails.competition.equals("AFL", ignoreCase = true)) {
                generateAFLTeamMarker(context, matchDetails)
            } else {
                generateLeagueMarker(context, matchDetails.competition)
            }
        } else {
            // Default marker for events without match details
            generateDefaultMarker(context)
        }
    }
    
    /**
     * Generate marker with AFL team logos
     */
    private fun generateAFLTeamMarker(context: Context, matchDetails: MatchDetails): BitmapDescriptor {
        val homeTeamLogo = TeamLogoMapper.getTeamLogoByName(matchDetails.homeTeam)
        val awayTeamLogo = TeamLogoMapper.getTeamLogoByName(matchDetails.awayTeam)
        
        return if (homeTeamLogo != null && awayTeamLogo != null) {
            // Create a marker with both team logos side by side
            generateDualTeamMarker(context, homeTeamLogo, awayTeamLogo)
        } else {
            // Fallback to league logo if team logos not found
            generateLeagueMarker(context, matchDetails.competition)
        }
    }
    
    /**
     * Generate marker with league logo
     */
    private fun generateLeagueMarker(context: Context, competition: String): BitmapDescriptor {
        val leagueLogoRes = when (competition.uppercase()) {
            "AFL" -> R.drawable.league_afl
            "A-LEAGUE" -> R.drawable.league_a_league
            "PREMIER LEAGUE" -> R.drawable.league_premier_league
            "NBA" -> R.drawable.league_nba
            else -> R.drawable.ic_league // Default league icon
        }
        
        return generateSingleLogoMarker(context, leagueLogoRes)
    }
    
    /**
     * Generate default marker for events without match details
     */
    private fun generateDefaultMarker(context: Context): BitmapDescriptor {
        return generateSingleLogoMarker(context, R.drawable.ic_sports)
    }
    
    /**
     * Generate a marker with a single logo
     */
    private fun generateSingleLogoMarker(context: Context, logoRes: Int): BitmapDescriptor {
        val density = context.resources.displayMetrics.density
        val pinWidth = (PIN_WIDTH_DP * density).toInt()
        val pinHeight = (PIN_HEIGHT_DP * density).toInt()
        val triangleHeight = (TRIANGLE_HEIGHT_DP * density).toInt()
        val logoSize = (LOGO_SIZE_DP * density).toInt()
        val cornerRadius = (CORNER_RADIUS_DP * density).toInt()
        val shadowOffset = (SHADOW_OFFSET_DP * density).toInt()
        val shadowBlur = (SHADOW_BLUR_DP * density).toInt()
        
        // Add extra space for shadow
        val totalHeight = pinHeight + triangleHeight + shadowOffset + shadowBlur
        val totalWidth = pinWidth + shadowOffset + shadowBlur
        
        // Create bitmap for the marker with shadow space
        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Draw shadow first
        paint.setShadowLayer(shadowBlur.toFloat(), shadowOffset.toFloat(), shadowOffset.toFloat(), Color.argb(80, 0, 0, 0))
        
        // Draw white rounded rectangle background
        paint.color = Color.WHITE
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
        
        // Clear shadow for logo
        paint.clearShadowLayer()
        
        // Draw logo in center of rounded rectangle
        val logoDrawable = ContextCompat.getDrawable(context, logoRes)
        if (logoDrawable != null) {
            val logoLeft = (pinWidth - logoSize) / 2
            val logoTop = (pinHeight - logoSize) / 2
            val logoRight = logoLeft + logoSize
            val logoBottom = logoTop + logoSize
            
            logoDrawable.setBounds(logoLeft, logoTop, logoRight, logoBottom)
            logoDrawable.draw(canvas)
        }
        
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    
    /**
     * Generate a marker with two team logos side by side
     */
    private fun generateDualTeamMarker(context: Context, homeTeamLogo: Int, awayTeamLogo: Int): BitmapDescriptor {
        val density = context.resources.displayMetrics.density
        val pinWidth = (PIN_WIDTH_DP * density).toInt()
        val pinHeight = (PIN_HEIGHT_DP * density).toInt()
        val triangleHeight = (TRIANGLE_HEIGHT_DP * density).toInt()
        val logoSize = (LOGO_SIZE_DP * density).toInt()
        val cornerRadius = (CORNER_RADIUS_DP * density).toInt()
        val shadowOffset = (SHADOW_OFFSET_DP * density).toInt()
        val shadowBlur = (SHADOW_BLUR_DP * density).toInt()
        
        // Add extra space for shadow
        val totalHeight = pinHeight + triangleHeight + shadowOffset + shadowBlur
        val totalWidth = pinWidth + shadowOffset + shadowBlur
        
        // Create bitmap for the marker with shadow space
        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Draw shadow first
        paint.setShadowLayer(shadowBlur.toFloat(), shadowOffset.toFloat(), shadowOffset.toFloat(), Color.argb(80, 0, 0, 0))
        
        // Draw white rounded rectangle background
        paint.color = Color.WHITE
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
        
        // Clear shadow for logos
        paint.clearShadowLayer()
        
        // Draw home team logo on the left
        val homeLogoDrawable = ContextCompat.getDrawable(context, homeTeamLogo)
        if (homeLogoDrawable != null) {
            val logoSpacing = 8 * density // 8dp spacing between logos for better horizontal padding
            val totalLogoWidth = (logoSize * 2) + logoSpacing
            val startX = (pinWidth - totalLogoWidth) / 2
            
            val homeLogoLeft = startX.toInt()
            val logoTop = (pinHeight - logoSize) / 2
            val homeLogoRight = homeLogoLeft + logoSize
            val logoBottom = logoTop + logoSize
            
            homeLogoDrawable.setBounds(homeLogoLeft, logoTop, homeLogoRight, logoBottom)
            homeLogoDrawable.draw(canvas)
        }
        
        // Draw away team logo on the right
        val awayLogoDrawable = ContextCompat.getDrawable(context, awayTeamLogo)
        if (awayLogoDrawable != null) {
            val logoSpacing = 8 * density // 8dp spacing between logos for better horizontal padding
            val totalLogoWidth = (logoSize * 2) + logoSpacing
            val startX = (pinWidth - totalLogoWidth) / 2
            
            val awayLogoLeft = (startX + logoSize + logoSpacing).toInt()
            val logoTop = (pinHeight - logoSize) / 2
            val awayLogoRight = awayLogoLeft + logoSize
            val logoBottom = logoTop + logoSize
            
            awayLogoDrawable.setBounds(awayLogoLeft, logoTop, awayLogoRight, logoBottom)
            awayLogoDrawable.draw(canvas)
        }
        
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
