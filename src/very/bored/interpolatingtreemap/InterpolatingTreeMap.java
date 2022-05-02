package very.bored.interpolatingtreemap;

import very.bored.regressionfactories.LinRegEquationFactory;
import very.bored.regressionfactories.QuadRegEquationFactory;

import java.util.*;
import java.util.function.Function;

/**
 * A roughly Map-based class that creates a regression line based on the inputted data
 * and the implementation of the {@link RegressionEquationFactory} interface.
 * <p>
 * Mapping functions are delegated to the {@link TreeMap} class, chosen for its natural ordering
 * of the coordinates. Composition was favored over inheritance so as to have greater control
 * over the modification of the composed {@link TreeMap} in this class to prevent interference,
 * intentional or not, with the regression line.
 * <p>
 * The type of regression line is determined by the implementation of the
 * {@link RegressionEquationFactory} injected. Such implementations include {@link LinRegEquationFactory}
 * or {@link QuadRegEquationFactory}
 * <p>
 * Note that usage of this class is NOT synchronized.
 */
@SuppressWarnings("unchecked")
public class InterpolatingTreeMap {

    /**
     * The TreeMap which holds all the (x, y) coordinates (as {@link Double Doubles}) inputted from which the
     * regression lines are generated.
     */
    private final TreeMap<Double, Double> dataPoints = new TreeMap<>();

    /**
     * The {@link RegressionEquationFactory} implementation which is used to generate the
     * regression line from the data points.
     */
    private final RegressionEquationFactory regressionEquationFactory;

    /**
     * The generated regression function used to predict an inputted value. Inputs
     * an x value and returns the corresponding y-hat. Regenerated every time the dataset
     * is added to or detracted from.
     */
    private Function<Double, Double> cachedRegressionEquation;

    /**
     * Constructor for the InterpolatingTreeMap.
     *
     * @param regressionEquationFactory The desired implementation of the RegressionEquationFactory.
     * @param entries                   The entries to be added to the InterpolatingTreeMap. At least one entry is required.
     * @throws IllegalArgumentException If there are no entries.
     * @throws NullPointerException     If the regressionEquationFactory or any entry is null.
     */
    @SafeVarargs
    public InterpolatingTreeMap(
            RegressionEquationFactory regressionEquationFactory,
            Map.Entry<Double, Double>... entries
    ) {
        this.regressionEquationFactory = Objects.requireNonNull(regressionEquationFactory);

        validateSize(entries);

        for (var entry : entries) {
            dataPoints.put(
                    Objects.requireNonNull(entry.getKey()),
                    Objects.requireNonNull(entry.getValue())
            );
        }

        this.cachedRegressionEquation = regressionEquationFactory.equationFrom(dataPoints);
    }

    /**
     * Helper function for the constructor. Ensures that there is at least the minimum number of entries.
     * The minimum number of entries is taken from the {@link RegressionEquationFactory} implementation,
     * as different regressions require different numbers of entries to generate a function.
     *
     * @param entries The entries whose size is to be validated.
     * @throws IllegalArgumentException If there are less than the required minimum number of entries.
     */
    private void validateSize(Map.Entry<Double, Double>[] entries) {
        if (entries.length < regressionEquationFactory.getMinEntries())
            throw new IllegalArgumentException(
                    "Need at least" + regressionEquationFactory.getMinEntries() + "entr"
                            + (regressionEquationFactory.getMinEntries() != 1 ? "ies" : "y")
                            + " to generate a regression line for this factory.");
    }

    /**
     * Returns the y-hat value for the inputted x value based on the cached regression function.
     * The y-hat is calculated from the cached regression function regardless of if the inputted value is in
     * dataPoints.
     *
     * @param x The x value to be predicted.
     * @return The y-hat value for the inputted x value.
     */
    public Double get(double x) {
        return cachedRegressionEquation.apply(x);
    }

    /**
     * Returns an {@link Optional} of the exact corresponding y value for the inputted x value <u>if
     * and only if</u> the inputted x value is in dataPoints (Does not use regression). Otherwise,
     * returns an empty Optional.
     *
     * @param x The x value
     * @return An {@link Optional} of the corresponding y value.
     */
    public Optional<Double> getExact(double x) {
        return Optional.ofNullable(dataPoints.get(x));
    }

    /**
     * Returns a {@link String} representation of the cached regression equation generated from the
     * dataPoints. Concrete implementations of the {@link RegressionEquationFactory} interface are encouraged
     * to format the equation as follows, but it is not required nor guaranteed.
     * <pre>ŷ = a + bx + cx² + ...</pre>
     * Implementations return an {@link Optional}, but the equation will always be present here as the
     * function is always created as soon as this object is constructed.
     *
     * @return A {@link String} version of the regression equation.
     */
    public String getEquationAsString() {
        return regressionEquationFactory.equationAsString(dataPoints);
    }

    /**
     * Adds the inputted entries to dataPoints, then regenerates the cached regression function
     * to account for the new points. Can not be null.
     *
     * @param entries The entries to be added.
     * @throws NullPointerException if any entry is null.
     */
    @SafeVarargs
    public final void put(Map.Entry<Double, Double>... entries) {
        Objects.requireNonNull(entries);
        for (var entry : entries) {
            dataPoints.put(entry.getKey(), entry.getValue());
        }
        cachedRegressionEquation = regressionEquationFactory.equationFrom(dataPoints);
    }

    /**
     * Creates a duplicate of this InterpolatingTreeMap with the exact same dataset, but using
     * the new {@link RegressionEquationFactory} inputted. May be used for map duplication, setting a
     * new factory, or as such:
     * <pre>{@code
     *  double y_hat = myQuadraticInterpolatingTreeMap
     *      .with(new LinearRegressionEquationFactory())
     *      .get(x);
     * }</pre>
     *
     * @param regressionEquationFactory The new {@link RegressionEquationFactory} to be used.
     * @throws NullPointerException if regressionEquationFactory is null.
     */
    public InterpolatingTreeMap with(RegressionEquationFactory regressionEquationFactory) {
        return new InterpolatingTreeMap(
                Objects.requireNonNull(regressionEquationFactory),
                dataPoints.entrySet().toArray(new Map.Entry[0])
        );
    }

    /**
     * If present, removes the inputted value from dataPoints, then regenerates the cached regression function
     * to account for the modified dataset. Otherwise, this function does nothing.
     *
     * @param x The x value of the point to remove.
     * @throws IllegalStateException If removing a point would make the dataset smaller than the minimum size
     *                               required by the {@link RegressionEquationFactory} implementation to generate a regression equation.
     */
    public void remove(double x) {
        if (dataPoints.containsKey(x)) {
            if (dataPoints.size() == regressionEquationFactory.getMinEntries()) {
                throw new IllegalStateException("Cannot remove last data point(s); add another point first");
            }
            dataPoints.remove(x);
            cachedRegressionEquation = regressionEquationFactory.equationFrom(dataPoints);
        }
    }

    /**
     * Returns the coefficient of determination (R^2) of the regression function.
     *
     * @return The coefficient of determination (R^2) of the regression function.
     */
    public double getCoefficientOfDetermination() {
        return regressionEquationFactory.getCoefficientOfDetermination(dataPoints);
    }

    /**
     * Returns the {@link RegressionEquationFactory} used to generate the cached regression function.
     *
     * @return The {@link RegressionEquationFactory} used to generate the cached regression function.
     */
    //TODO: Prefer to return a copy of the factory instead
    public RegressionEquationFactory getRegressionEquationFactory() {
        return regressionEquationFactory;
    }

    /**
     * Returns the regression {@link Function} derived from the current points. Takes an inputted x value
     * as a double, and returns the predicted y-hat as a double as well.
     *
     * @return The regression {@link Function} derived from the current points.
     */
    public Function<Double, Double> getRegressionEquation() {
        return regressionEquationFactory.equationFrom(dataPoints);
    }

    /**
     * Returns a {@link TreeMap} copy of the dataset.
     *
     * @return A {@link TreeMap} copy of the dataset.
     */
    public TreeMap<Double, Double> getDataPoints() {
        return (TreeMap<Double, Double>) dataPoints.clone();
    }

    /**
     * Returns the size of the dataPoints.
     *
     * @return The size of the dataPoints.
     */
    public int datasetSize() {
        return dataPoints.size();
    }

    /**
     * Returns the minimum number of data points required to generate a regression function. The number
     * varies depending on the {@link RegressionEquationFactory} implementation, as different regression
     * equations require different numbers of data points to get an estimate.
     *
     * @return The minimum number of data points required to generate a regression function.
     */
    public int minimumDatasetSize() {
        return regressionEquationFactory.getMinEntries();
    }
}

