import java.io.BufferedReader

data class Matrix(var n: Double, var size: Int, var matrix: ArrayList<ArrayList<Double>>)

class MatrixBuilder {

    fun read(reader: BufferedReader): Matrix {
        /*
        Считать строку с погрешностью и размерностью
        Считать в цикле строки матрицы
         */
        return Matrix(1.0, 1, ArrayList<ArrayList<Double>>())
    }

    fun generateRandom(size: Int): Matrix {
        return Matrix(1.0, 1, ArrayList<ArrayList<Double>>())
    }
}