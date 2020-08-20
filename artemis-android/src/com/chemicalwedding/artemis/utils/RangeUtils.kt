package com.chemicalwedding.artemis.utils

class RangeUtils {
    companion object {
        fun transform(
                currentMin: Int,
                currentMax: Int,
                newMin: Int,
                newMax: Int,
                value: Int
        ): Int {
            val currentRange = currentMax - currentMin
            val newRange = newMax - newMin

            return (((value - currentMin) * newRange) / currentRange) + newMin
        }

        fun transform(
                currentMin: Int,
                currentMax: Int,
                newMin: Int,
                newMax: Int,
                value: Float
        ): Float {
            val currentRange = currentMax - currentMin
            val newRange = newMax - newMin

            return (((value - currentMin) * newRange) / currentRange) + newMin
        }
    }
}