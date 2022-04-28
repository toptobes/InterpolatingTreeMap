package very.bored.interpolatingtreemap;

import java.util.*;
import java.util.function.Function;

public class InterpolatingTreeMap {
    private final TreeMap<Double, Double> dataPoints = new TreeMap<>();

    private RegressionEquationFactory regressionEquationFactory;
    private Function<Double, Double> cachedRegressionEquation;

    @SafeVarargs
    private InterpolatingTreeMap(
            RegressionEquationFactory regressionEquationFactory,
            Map.Entry<Double, Double>... entries
    ) {
        for (var entry : entries) {
            dataPoints.put(entry.getKey(), entry.getValue());
        }

        this.regressionEquationFactory = regressionEquationFactory;
        this.cachedRegressionEquation = regressionEquationFactory.equationFrom(dataPoints);
    }

    public Double get(double x) {
        return cachedRegressionEquation.apply(x);
    }

    public Double getWith(double x, RegressionEquationFactory interpolationEquationFactory) {
        return interpolationEquationFactory.equationFrom(dataPoints).apply(x);
    }

    public Double getExactOrNull(double x) {
        return dataPoints.get(x);
    }

    @SafeVarargs
    public final void put(Map.Entry<Double, Double>... entries) {
        for (var entry : entries) {
            dataPoints.put(entry.getKey(), entry.getValue());
        }
        cachedRegressionEquation = regressionEquationFactory.equationFrom(dataPoints);
    }

    public RegressionEquationFactory getRegressionEquationFactory() {
        return regressionEquationFactory;
    }

    public Function<Double, Double> getRegressionEquation() {
        return regressionEquationFactory.equationFrom(dataPoints);
    }

    public void setRegressionEquationFactory(RegressionEquationFactory regressionEquationFactory) {
        this.regressionEquationFactory = regressionEquationFactory;
        cachedRegressionEquation = regressionEquationFactory.equationFrom(dataPoints);
    }

    @SuppressWarnings("unchecked")
    public static class Builder {
        private final ArrayList<Map.Entry<Double, Double>> dataPoints = new ArrayList<>();
        private RegressionEquationFactory interpolationEquationFactory;

        public Builder regressionEquationFactory(RegressionEquationFactory interpolationEquationFactory) {
            this.interpolationEquationFactory = interpolationEquationFactory;
            return this;
        }

        @SafeVarargs
        public final Builder dataPoints(Map.Entry<Double, Double>... entries) {
            if (this.dataPoints.size() < 2) {
                throw new IllegalArgumentException("Need at least 2 entries");
            }

            this.dataPoints.addAll(Arrays.asList(entries));
            return this;
        }

        public InterpolatingTreeMap build() {
            return new InterpolatingTreeMap(interpolationEquationFactory, dataPoints.toArray(new Map.Entry[0]));
        }
    }
}

