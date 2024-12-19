package com.grifffith.mindfuljournal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



val typography = Typography(
    // Body text - large with proper line height and letter spacing
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp, // Slightly larger for better readability
        lineHeight = 28.sp, // Increased line height for better text spacing
        letterSpacing = 0.5.sp // Adds slight letter spacing for clarity
    ),
    // Body text - medium (for smaller body text or supplementary info)
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Headline - large (for main titles or screen headings)
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold, // Bold weight for emphasis
        fontSize = 34.sp, // Large font size for prominent headings
        lineHeight = 40.sp, // Larger line height for improved spacing
        letterSpacing = 1.sp // Increased letter spacing for modern aesthetics
    ),
    // Headline - medium (for subheadings or secondary headings)
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // Medium weight for secondary headings
        fontSize = 26.sp, // Larger than body text for distinction
        lineHeight = 32.sp, // Suitable line height for subheadings
        letterSpacing = 0.5.sp
    ),
    // Title - large (for important section titles)
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold, // Bold for important titles
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Label - small (for smaller labels or tags)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium, // Medium weight for emphasis
        fontSize = 12.sp, // Smaller font size for labels
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Add more styles as needed, such as for buttons, captions, etc.
)
