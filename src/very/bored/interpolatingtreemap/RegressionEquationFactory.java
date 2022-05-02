package very.bored.interpolatingtreemap;

import very.bored.regressionfactories.LinRegEquationFactory;
import very.bored.regressionfactories.QuadRegEquationFactory;

import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * An object that can create a regression equation for a given set of data.
 * <p>
 * The type of regression depends on the implementation, but it is assumed that each function in the
 * interface follows the following rules:
 * <ul>
 *     <li>The functions are pure; that is, they do not depend on any state, nor do they modify
 *     any state. The output of the functions should always be the same for the same inputs
 *
 *     <li>The functions are not sensitive to the order of the data; A regression line based on the
 *     points {@code (1,2),(2,3),&(3,4)} should be the same as one based on {@code (3,4),(1,2),&(2,3)}
 *
 *     <li>The functions are consistent; for multiple invocations of the functions with the same dataset,
 *     the output should always be the same, aside from minute rounding errors due to floating point errors;
 *     {@code strictfp} is not required.
 * </ul>
 * The interface was created primarily for use with the {@link InterpolatingTreeMap} class, a roughly-Map
 * based object that estimates the corresponding ŷ of the given x value based on the given dataset and the
 * specific implementation of this interface
 *
 * @see InterpolatingTreeMap
 * @see LinRegEquationFactory
 * @see QuadRegEquationFactory
 */
public interface RegressionEquationFactory {

    /**
     * Creates the regression equation for the given set of data, passed as a {@link TreeMap}. The equation
     * is returned as a {@link Function}, which should take an inputted x value and return the corresponding
     * ŷ value based on the regression line. Both the x and y values are {@link Double Doubles}.
     *
     * <p>Function is encouraged to follow the rules above</p>
     *
     * @param dataPoints The data to use to create the regression equation
     * @return The regression equation function
     */
    Function<Double, Double> equationFrom(TreeMap<Double, Double> dataPoints);

    /**
     * Returns the coefficient of determination (R²) for the given data. The coefficient of determination
     * is a measure of how well the regression line fits the data. It is a value between 0 and 1, where
     * 1 is perfect fit and 0 is no fit.
     * <p>
     * The implementation may use the following formula: (Although there are a couple alternatives)
     * <pre>R² = 1 - (sum of squared residuals / sum of squared total)</pre>
     * where:
     * <ul>
     *     <li>sum of squared residuals = sum of squared differences between the actual y values and the
     *     predicted y values (y - ŷ)²
     *     <li>sum of squared total = sum of squared differences between the actual y values and the mean
     *     of the actual y values (y - ȳ)²
     * </ul>
     *
     * @param dataPoints The data used to calculate the coefficient of determination
     * @return The coefficient of determination
     */
    double getCoefficientOfDetermination(TreeMap<Double, Double> dataPoints);

    /**
     * Returns the regression equation in the form of a string. Implementations are encouraged to
     * format the returned string as follows:
     * <pre>ŷ = a + bx + cx² + ...</pre>
     * However, this is not required; simply encouraged
     *
     * @param dataPoints The data used to create the regression equation
     * @return The regression equation in the form of a string
     */
    String equationAsString(TreeMap<Double, Double> dataPoints);

    /**
     * Returns the minimum number of data points required to create a regression equation.
     * <p>
     * Different regression equations require different numbers of data points; for example,
     * a linear regression requires only two data points, while a quadratic regression requires
     * at least three data points, as there are an infinite number of quadratic equations
     * that contain any two or any one data point(s)
     *
     * @return The minimum number of data points required to create a regression equation.
     */
    int getMinEntries();
}
