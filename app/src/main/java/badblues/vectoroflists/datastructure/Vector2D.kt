package badblues.vectoroflists.datastructure

import kotlin.math.sqrt

public data class Vector2D(var x: Double, var y: Double) : Comparable<Vector2D> {
    fun calculateMagnitude(): Double {
        return sqrt(x * x + y * y)
    }

    fun normalize() {
        val magnitude = calculateMagnitude()
        if (magnitude != 0.0) {
            x /= magnitude
            y /= magnitude
        }
    }

    fun add(other: Vector2D): Vector2D {
        return Vector2D(x + other.x, y + other.y)
    }

    fun subtract(other: Vector2D): Vector2D {
        return Vector2D(x - other.x, y - other.y)
    }

    override fun compareTo(other: Vector2D): Int {
        val thisMagnitude = this.calculateMagnitude()
        val otherMagnitude = other.calculateMagnitude()
        return thisMagnitude.compareTo(otherMagnitude)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    companion object {
        @JvmStatic
        fun parseVector2d(input: String): Vector2D {
            try {
                val parts = input.split(",")

                if (parts.size != 2) {
                    throw IllegalArgumentException("Input must contain two values separated by a delimiter.")
                }

                val x = parts[0].trim().toDouble()
                val y = parts[1].trim().toDouble()

                return Vector2D(x, y)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid input format. Must be in the format 'x, y'.", e)
            }
        }
    }
}
