package very.bored.regressionfactories;

import very.bored.interpolatingtreemap.RegressionEquationFactory;

import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

public class LinRegEquationFactory implements RegressionEquationFactory {
    private String equation;

    @Override
    public Function<Double, Double> equationFrom(TreeMap<Double, Double> dataPoints) {
        double SSxx = calcSSxx(dataPoints);
        double SSxy = calcSSxy(dataPoints);
        double b = SSxy / SSxx;
        double a = calcIntercept(SSxx, SSxy, dataPoints);

        equation = String.format("f(x) = %.2f + %.2fx", a, b);

        return x -> (a) + (b * x);
    }

    @Override
    public double getCoefficientOfDetermination(TreeMap<Double, Double> dataPoints) {
        double SSE = calcSSE(dataPoints);
        double SST = calcSST(dataPoints);

        return 1 - SSE / SST;
    }

    private double calcIntercept(double SSxx, double SSxy, TreeMap<Double, Double> map) {
        AvgXY means = AvgXY.from(map);
        return means.y - (SSxy * means.x / SSxx);
    }

    private double calcSSxx(TreeMap<Double, Double> map) {
        AvgXY means = AvgXY.from(map);

        double SSxx = 0.0;
        for (Double x : map.keySet()) {
            SSxx += (x - means.x) * (x - means.x);
        }

        return SSxx;
    }

    private double calcSSxy(TreeMap<Double, Double> map) {
        AvgXY means = AvgXY.from(map);

        double SSxy = 0;
        for (var entry : map.entrySet()) {
            SSxy += (entry.getKey() - means.x) * (entry.getValue() - means.y);
        }

        return SSxy;
    }

    private double calcSSE(TreeMap<Double, Double> map) {
        var predictor = equationFrom(map);

        double SSE = 0;
        for (var entry : map.entrySet()) {
            SSE += (entry.getValue() - predictor.apply(entry.getKey())) * (entry.getValue() - predictor.apply(entry.getKey()));
        }

        return SSE;
    }

    private double calcSST(TreeMap<Double, Double> map) {
        AvgXY means = AvgXY.from(map);

        double SST = 0;
        for (var entry : map.entrySet()) {
            SST += (entry.getValue() - means.y) * (entry.getValue() - means.y);
        }

        return SST;
    }

    private record AvgXY(double x, double y) {
        static AvgXY from(TreeMap<Double, Double> map) {
            double avgX = 0, avgY = 0;
            for (var entry : map.entrySet()) {
                avgX += entry.getKey();
                avgY += entry.getValue();
            }

            return new AvgXY(avgX / map.size(), avgY / map.size());
        }
    }

    @Override
    public String equationAsString(TreeMap<Double, Double> dataPoints) {
        return equation;
    }

    @Override
    public int getMinEntries() {
        return 2;
    }
}