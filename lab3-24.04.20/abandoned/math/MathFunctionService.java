package math;

import java.util.HashSet;
import static java.lang.Math.*;

public class MathFunctionService {

    public enum SolveMethods {
        NEWTON_POLYNOMIAL { @Override public String toString() { return "Интерполирование многочленом Ньютона"; }},
        LAGRANGE_POLYNOMIAL { @Override public String toString() { return "Интерполирование многочленом Лагранжа"; }},
        CUBIC_SPLINE { @Override public String toString() { return "Интерполирование кубическими сплайнами"; }}
    }

    //TODO: норм объекты
    private static final Object solver;
    public static final HashSet<MathFunction> equations;

    static {
        solver = new InterpolationSolver();
        equations = new HashSet<>();
        equations.add(new MathFunction() {
            @Override public double func(double x) { return sin(x); }
            @Override public String toString() { return "y = sin(x)"; }
        });
    }

    public static InterpolationAnswer solve() {
        //TODO: выбор метода
        return new InterpolationAnswer();
    }
}
