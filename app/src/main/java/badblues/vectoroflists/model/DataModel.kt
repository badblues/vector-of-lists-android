package badblues.vectoroflists.model

import badblues.vectoroflists.datastructure.VectorOfLists
import badblues.vectoroflists.datastructure.Vector2D

public class DataModel private constructor() {
    companion object {
        private var instance: DataModel? = null

        fun getInstance(): DataModel {
            if (instance == null) {
                instance = DataModel()
            }
            return instance!!
        }
    }

    private var baseCapacity = 2
    private var currentType = DataTypes.Double
    private var doubles: VectorOfLists<Double> = VectorOfLists(baseCapacity)
    private var vectors2D: VectorOfLists<Vector2D> = VectorOfLists(baseCapacity)

    fun getDoublesVector(): VectorOfLists<Double> {
        return doubles
    }

    fun getVectors2DVector(): VectorOfLists<Vector2D> {
        return vectors2D
    }

    fun clearVectors() {
        doubles = VectorOfLists(baseCapacity)
        vectors2D = VectorOfLists(baseCapacity)
    }

    fun changeBaseCapacities(newCapacity: Int) {
        baseCapacity = newCapacity
        clearVectors()
    }

    fun getCurrentType(): DataTypes {
        return currentType
    }

    fun setCurrentType(type: DataTypes) {
        currentType = type
        clearVectors()
    }

    fun setDoublesVector(vector: VectorOfLists<Double>) {
        doubles = vector
        baseCapacity = doubles.getBaseCapacity()
    }

    fun setVectors2DVector(vector: VectorOfLists<Vector2D>) {
        vectors2D = vector
        baseCapacity = vectors2D.getBaseCapacity()
    }
}
