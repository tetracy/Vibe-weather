package com.vibe.weather

import androidx.compose.ui.graphics.Color
import com.vibe.weather.ui.theme.ProbTier
import com.vibe.weather.ui.theme.probTier
import com.vibe.weather.ui.theme.probTierColor
import com.vibe.weather.ui.theme.temperatureTierColor
import org.junit.Assert.assertEquals
import org.junit.Test

class PaletteTest {

    @Test
    fun temperatureTiersFollowAppleBands() {
        assertEquals(Color(0xFF2E5FA3), temperatureTierColor(-5))
        assertEquals(Color(0xFF5AAFFA), temperatureTierColor(0))
        assertEquals(Color(0xFF5AAFFA), temperatureTierColor(14))
        assertEquals(Color(0xFF6BBF59), temperatureTierColor(15))
        assertEquals(Color(0xFFE8C547), temperatureTierColor(20))
        assertEquals(Color(0xFFF08C4A), temperatureTierColor(25))
        assertEquals(Color(0xFFE05252), temperatureTierColor(30))
    }

    @Test
    fun probTiersBreakAt40And70() {
        assertEquals(ProbTier.LOW, probTier(39))
        assertEquals(ProbTier.MID, probTier(40))
        assertEquals(ProbTier.MID, probTier(69))
        assertEquals(ProbTier.HIGH, probTier(70))
    }

    @Test
    fun probTierColorsStayDistinct() {
        val low = probTierColor(10)
        val mid = probTierColor(50)
        val high = probTierColor(80)
        assert(low != mid && mid != high)
    }
}
