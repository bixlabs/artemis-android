package com.chemicalwedding.artemis.utils

import android.util.Log

class ColorUtils {
    companion object {
        private val TAG = ColorUtils::class.java.canonicalName

        fun colorTemperatureToRGBColor(whiteBalance: Int): Color {
            val temperature = whiteBalance / 100f;
            var red: Float
            var green: Float
            var blue: Float

            //Calculate red
            if (temperature <= 66) red = 255f else {
                red = temperature - 60
                red = (329.698727446 * Math.pow(red.toDouble(), -0.1332047592)).toFloat()
                if (red < 0) red = 0f
                if (red > 255) red = 255f
            }

            //Calculate green
            if (temperature <= 66) {
                green = temperature
                green =
                        (99.4708025861 * Math.log(green.toDouble()) - 161.1195681661).toFloat()
                if (green < 0) green = 0f
                if (green > 255) green = 255f
            } else {
                green = temperature - 60
                green = (288.1221695283 * Math.pow(
                        green.toDouble(),
                        -0.0755148492
                )).toFloat()
                if (green < 0) green = 0f
                if (green > 255) green = 255f
            }

            //calculate blue
            if (temperature >= 66) blue = 255f else if (temperature <= 19) blue = 0f else {
                blue = temperature - 10
                blue =
                        (138.5177312231 * Math.log(blue.toDouble()) - 305.0447927307).toFloat()
                if (blue < 0) blue = 0f
                if (blue > 255) blue = 255f
            }

            Log.v(TAG, "${whiteBalance}K = RGB($red, $green, $blue)")

            red /= 255
            green /= 255
            blue /= 255

            return Color(red, green, blue)
        }
    }

    data class Color(
            val red: Float,
            val green: Float,
            val blue: Float
    ) {
        operator fun times(b: Color): Color =
                Color(red * b.red, green * b.green, blue * b.blue)

        override fun toString(): String {
            return "RGB($red, $green, $blue)"
        }
    }
}
