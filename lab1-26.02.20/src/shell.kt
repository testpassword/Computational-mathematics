fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        val matrix: ArrayList<ArrayList<Double>>
        val supporter = MatrixSupporter()
        when {
            (args[0] == "-f") -> matrix = supporter.inputFromFile()
            (args[0] == "-k") -> matrix = supporter.inputFromKeyboard()
            (args[0] == "-r") -> matrix = supporter.generateRandom()
        }
        MatrixSolver.solveByGaussSeidel(matrix)
    } else println(""""Программа запущена без аргументов. Используйте ключи:
                        ~   -f для чтения матрицы из файла,
                        ~   -k для ввода матрицы с клавиатуры,
                        ~   -r для генерирования случайной правильной матрицы""".trimMargin("~")
    )
}