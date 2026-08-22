package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.Direction
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Compass09", appName)
    }

    @Test
    fun `direction from degrees mapping`() {
        assertEquals(Direction.NORTH, Direction.fromDegrees(0f))
        assertEquals(Direction.NORTH, Direction.fromDegrees(10f))
        assertEquals(Direction.NORTH, Direction.fromDegrees(350f))
        assertEquals(Direction.NORTH_EAST, Direction.fromDegrees(45f))
        assertEquals(Direction.EAST, Direction.fromDegrees(90f))
        assertEquals(Direction.SOUTH_EAST, Direction.fromDegrees(135f))
        assertEquals(Direction.SOUTH, Direction.fromDegrees(180f))
        assertEquals(Direction.SOUTH_WEST, Direction.fromDegrees(225f))
        assertEquals(Direction.WEST, Direction.fromDegrees(270f))
        assertEquals(Direction.NORTH_WEST, Direction.fromDegrees(315f))
    }
}
