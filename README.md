# Лабораторная 1 - Решение системы линейных алгебраических уравнений СЛАУ #

Варианты:
1. Метод Гаусса
2. Метод Гаусса с выбором главного элемента
3. Метод простых итераций
4. **Метод Гаусса-Зейделя**

Размерность `n <= 20` (задается из файла или с клавиатуры - по выбору конечного пользователя). 

Должно быть предусмотрено чтение исходных данных как из файла, так и ввод с клавиатуры.

Должна быть реализована возможность ввода коэффициентов матрицы как с клавиатуры, так и из файла. Также предусмотреть случайные коэффициенты.

Обязательно: Тестовые данные на матрице большого размера ( `5*5` / `6 * 6` ...) + в отчёт с решением.

Для точных методов (Гаусс и главные элементы) должно быть реализовано:
- Вычисление определителя
- Вывод треугольной  матрицы (включая преобразованный столбец В)
- Столбец неизвестных
- Столбец невязок

Для итерационных методов:
- Точность задается с клавиатуры/файла
- Проверка диагонального преобладания
//В случае, если диагональное преобладание в изначальной матрице отсутствует - предлагается сделать перестановку строк/столбцов до тех пор, пока преобладание не будет достигнуто. В случае невозможности достижения диагонального преобладания - выводить сообщение.
- Столбец неизвестных
- Количество итераций, за которое было найдено решение
- Столбец погрешностей

# Лабораторная 2 - Интегрирование #

Варианты:
1. Метод прямоугольников (должен быть реализован расчет 3-мя модификациями: левые, правые, средние)
2. **Метод трапеций**
3. Метод Симпсона

Пользователь выбирает функцию, интеграл которой он хочет вычислить (3-5 функций), из тех, которые предлагает программа.

В численный метод должен быть передан параметр-агрегат на подпрограмму вычисления значения функции в точке `x`.

Пользователь задает пределы интегрирования и точность. 

NOTE! Если нижний предел интегрирования >= верхнего предела - интеграл должен считаться корректно!

В результате должны получить:
- значение интеграла
- количество разбиений, на которое пришлось разбить
- полученную погрешность 

Для оценки погрешности использовать оценку Рунге.