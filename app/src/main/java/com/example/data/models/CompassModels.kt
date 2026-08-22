package com.example.data.models

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElementAir
import com.example.ui.theme.ElementEarth
import com.example.ui.theme.ElementFire
import com.example.ui.theme.ElementSpace
import com.example.ui.theme.ElementWater

enum class SensorAccuracy(val label: String, val description: String) {
    HIGH("High", "Sensors are highly calibrated and accurate."),
    MEDIUM("Medium", "Good accuracy. Minor interference detected."),
    LOW("Low", "Calibration needed. Sensor accuracy is low."),
    UNRELIABLE("Unreliable", "Severe magnetic interference. Figure-8 calibration required."),
    NO_SENSOR("Simulated", "Hardware compass sensor not detected.")
}

enum class Direction(
    val code: String,
    val fullName: String,
    val centerDegree: Float,
    val vastuHeading: String,
    val vastusummary: String,
    val element: String,
    val elementColor: Color,
    val sanskritName: String,
    val deityOrEnergy: String,
    val recommendedZones: String,
    val vastuDoAndDonts: String
) {
    NORTH(
        code = "N",
        fullName = "North",
        centerDegree = 0f,
        vastuHeading = "Career and opportunities",
        vastusummary = "North is ruled by Kuber, the lord of wealth. Ideal for attracting career prosperity, financial opportunities, and clear focus.",
        element = "Water (Jal)",
        elementColor = ElementWater,
        sanskritName = "Uttar (उत्तर)",
        deityOrEnergy = "Lord Kuber (Wealth & Abundance)",
        recommendedZones = "Home office, entrance, cash safe, living room, water fountains",
        vastuDoAndDonts = "Do keep this area light and clutter-free. Avoid placing heavy storage or master bedroom in this corner."
    ),
    NORTH_EAST(
        code = "NE",
        fullName = "North-East",
        centerDegree = 45f,
        vastuHeading = "Prayer, meditation, study",
        vastusummary = "The most sacred Ishanya corner where divine energy flows. Enhances spiritual growth, mental clarity, and memory power.",
        element = "Water & Ether",
        elementColor = ElementWater,
        sanskritName = "Ishanya (ईशान्य)",
        deityOrEnergy = "Lord Shiva / Divine Consciousness",
        recommendedZones = "Puja mandir, meditation altar, library, study desk, open balcony",
        vastuDoAndDonts = "Keep this sector extremely clean and open. Avoid toilets, kitchens, or heavy overhead tanks here."
    ),
    EAST(
        code = "E",
        fullName = "East",
        centerDegree = 90f,
        vastuHeading = "Health and new beginnings",
        vastusummary = "East is bathed in morning solar rays by Surya. Infuses physical vitality, vibrant health, and thriving social networks.",
        element = "Air & Sun (Surya)",
        elementColor = ElementAir,
        sanskritName = "Purva (पूर्व)",
        deityOrEnergy = "Lord Indra & Sun God Surya",
        recommendedZones = "Main entrance, family living area, study space, large windows",
        vastuDoAndDonts = "Ensure plenty of natural ventilation. Avoid tall obstructions or dark blocking walls on the east side."
    ),
    SOUTH_EAST(
        code = "SE",
        fullName = "South-East",
        centerDegree = 135f,
        vastuHeading = "Kitchen and fire elements",
        vastusummary = "Ruled by Agni, the cosmic flame. Governs cooking, digestion, vitality, metabolic power, and financial liquidity.",
        element = "Fire (Agni)",
        elementColor = ElementFire,
        sanskritName = "Agneya (आग्नेय)",
        deityOrEnergy = "Lord Agni (The Sacred Fire)",
        recommendedZones = "Kitchen cooktop, electrical distribution board, solar inverters, boiler",
        vastuDoAndDonts = "Ideal zone for gas stoves facing east. Avoid placing underground water tanks, borewells, or mirrors here."
    ),
    SOUTH(
        code = "S",
        fullName = "South",
        centerDegree = 180f,
        vastuHeading = "Fame and recognition",
        vastusummary = "Associated with stability, courage, and public prestige. Provides confidence and legal/business reputation.",
        element = "Earth & Fire",
        elementColor = ElementEarth,
        sanskritName = "Dakshin (दक्षिण)",
        deityOrEnergy = "Lord Yama (Dharma & Justice)",
        recommendedZones = "Secondary bedrooms, heavy furniture, office cabins, storage rooms",
        vastuDoAndDonts = "Keep walls thicker and taller here. Avoid large main water bodies or front porches in the pure south."
    ),
    SOUTH_WEST(
        code = "SW",
        fullName = "South-West",
        centerDegree = 225f,
        vastuHeading = "Master bedroom and stability",
        vastusummary = "The Nairutya corner represents the Earth element. Essential for leadership authority, grounded relationships, and marital peace.",
        element = "Earth (Prithvi)",
        elementColor = ElementEarth,
        sanskritName = "Nairutya (नैऋत्य)",
        deityOrEnergy = "Nairuti (Ancestral Strength & Grounding)",
        recommendedZones = "Master bedroom, head of family suite, cash vault, heavy wardrobes",
        vastuDoAndDonts = "Sleep with head facing South or East. Avoid puja rooms, kitchens, or open borewells in this sector."
    ),
    WEST(
        code = "W",
        fullName = "West",
        centerDegree = 270f,
        vastuHeading = "Creativity and children",
        vastusummary = "Governed by Varuna, ruler of rainfall and gains. Boosts creative expression, successful trade, and children's joy.",
        element = "Space & Water",
        elementColor = ElementSpace,
        sanskritName = "Paschim (पश्चिम)",
        deityOrEnergy = "Lord Varuna (Cosmic Order & Trade)",
        recommendedZones = "Dining room, children's bedroom, conference room, study room",
        vastuDoAndDonts = "Good for dining spaces where families gather. Avoid having large lower depressions or slopes to the west."
    ),
    NORTH_WEST(
        code = "NW",
        fullName = "North-West",
        centerDegree = 315f,
        vastuHeading = "Guests and relationships",
        vastusummary = "Ruled by Vayu, the wind element. Regulates social connections, healthy movement, guests, and beneficial friendships.",
        element = "Air (Vayu)",
        elementColor = ElementAir,
        sanskritName = "Vayavya (वायव्य)",
        deityOrEnergy = "Lord Vayu (Wind & Flow)",
        recommendedZones = "Guest bedroom, newly married couple room, pantry, vehicle garage",
        vastuDoAndDonts = "Great for guest hosting and outgoing inventory. Avoid keeping heavy immovable master beds permanently here."
    );

    companion object {
        fun fromDegrees(deg: Float): Direction {
            val normalized = (deg % 360f + 360f) % 360f
            return when {
                normalized >= 337.5f || normalized < 22.5f -> NORTH
                normalized < 67.5f -> NORTH_EAST
                normalized < 112.5f -> EAST
                normalized < 157.5f -> SOUTH_EAST
                normalized < 202.5f -> SOUTH
                normalized < 247.5f -> SOUTH_WEST
                normalized < 292.5f -> WEST
                else -> NORTH_WEST
            }
        }
    }
}

data class CompassData(
    val azimuthDegrees: Float = 0f,
    val pitchDegrees: Float = 0f,
    val rollDegrees: Float = 0f,
    val magneticFieldUt: Float = 45.0f,
    val accuracy: SensorAccuracy = SensorAccuracy.HIGH,
    val isHardwareSensor: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
) {
    val direction: Direction get() = Direction.fromDegrees(azimuthDegrees)
    
    val formattedDegrees: String get() = "${azimuthDegrees.toInt()}°"
    
    val formattedDirectionDegree: String get() = "${direction.code} (${azimuthDegrees.toInt()}°)"
    
    // Within 2 degrees of level phone
    val isLevel: Boolean get() = kotlin.math.abs(pitchDegrees) < 3.0f && kotlin.math.abs(rollDegrees) < 3.0f
    
    // Within 2 degrees of pure North
    val isPointingNorth: Boolean
        get() {
            val norm = (azimuthDegrees % 360f + 360f) % 360f
            return norm <= 2.0f || norm >= 358.0f
        }
}

data class AppSettings(
    val keepScreenOn: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val showLevelBubble: Boolean = true,
    val showVastuCard: Boolean = true,
    val lockedTargetBearing: Float? = null,
    val magneticDeclination: Float = 0f
)
