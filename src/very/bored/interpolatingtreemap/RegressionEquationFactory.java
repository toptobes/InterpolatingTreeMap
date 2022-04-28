package very.bored.interpolatingtreemap;

import java.util.TreeMap;
import java.util.function.Function;

public interface RegressionEquationFactory {
    Function<Double, Double> equationFrom(TreeMap<Double, Double> dataPoints);
}
