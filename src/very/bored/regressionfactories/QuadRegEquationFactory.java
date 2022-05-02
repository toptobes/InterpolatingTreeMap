package very.bored.regressionfactories;

import very.bored.interpolatingtreemap.RegressionEquationFactory;

import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

public class QuadRegEquationFactory implements RegressionEquationFactory {
    private String equation;

    @Override
    public Function<Double, Double> equationFrom(TreeMap<Double, Double> dataPoints) {
        ABC abc = ABC.from(dataPoints);

        equation = String.format("f(x) = %.5f + %.5fx + %.5fx^2", abc.a, abc.b, abc.c);

        return x -> (abc.a) + (abc.b * x) + (abc.c * x * x);
    }

    @Override
    public double getCoefficientOfDetermination(TreeMap<Double, Double> dataPoints) {
        double SSE = calcSSE(dataPoints);
        double SST = calcSST(dataPoints);

        return 1 - (SSE / SST);
    }

    private record ABC(double a, double b, double c) {
        public static ABC from(TreeMap<Double, Double> dataPoints) {
            AvgX2XY.invalidateCache();

            double SSxx = calcSSxx(dataPoints);
            double SSxy = calcSSxy(dataPoints);
            double SSxx2 = calcSSxx2(dataPoints);
            double SSx2x2 = calcSSx2x2(dataPoints);
            double SSx2y = calcSSx2y(dataPoints);
            AvgX2XY means = AvgX2XY.from(dataPoints);

            double c = (SSx2y * SSxx - SSxy * SSxx2) / (SSxx * SSx2x2 - SSxx2 * SSxx2);
            double b = (SSxy * SSx2x2 - SSx2y * SSxx2) / (SSxx * SSx2x2 - SSxx2 * SSxx2);
            double a = means.y - b * means.x - c * means.x2;

            return new ABC(a, b, c);
        }
    }

    private static double calcSSxx(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSxx = 0.0;
        for (Double x : map.keySet()) {
            SSxx += (x - means.x) * (x - means.x);
        }

        return SSxx;
    }

    private static double calcSSxy(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSxy = 0;
        for (var entry : map.entrySet()) {
            SSxy += (entry.getKey() - means.x) * (entry.getValue() - means.y);
        }

        return SSxy;
    }

    private static double calcSSxx2(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSxx2 = 0;
        for (Double x : map.keySet()) {
            SSxx2 += (x - means.x) * (x * x - means.x2);
        }

        return SSxx2;
    }

    private static double calcSSx2x2(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSx2x2 = 0;
        for (Double x : map.keySet()) {
            SSx2x2 += (x * x - means.x2) * (x * x - means.x2);
        }

        return SSx2x2;
    }

    private static double calcSSx2y(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSx2y = 0;
        for (var entry : map.entrySet()) {
            SSx2y += (entry.getKey() * entry.getKey() - means.x2) * (entry.getValue() - means.y);
        }

        return SSx2y;
    }

    private double calcSSE(TreeMap<Double, Double> dataPoints) {
        ABC abc = ABC.from(dataPoints);

        double SSE = 0;
        for (var entry : dataPoints.entrySet()) {
            SSE += Math.pow(entry.getValue() - abc.a - abc.b * entry.getKey() - abc.c * entry.getKey() * entry.getKey(), 2);
        }

        return SSE;
    }

    private double calcSST(TreeMap<Double, Double> dataPoints) {
        AvgX2XY means = AvgX2XY.from(dataPoints);

        double SST = 0;
        for (var entry : dataPoints.entrySet()) {
            SST += (entry.getValue() - means.y) * (entry.getValue() - means.y);
        }

        return SST;
    }

    private record AvgX2XY(double x2, double x, double y) {

        static AvgX2XY cache = null;

        private static void invalidateCache() {
            cache = null;
        }

        static AvgX2XY from(TreeMap<Double, Double> map) {
            if (cache != null) {
                return cache;
            }

            double avgX2 = 0, avgX = 0, avgY = 0;
            for (var entry : map.entrySet()) {
                avgX2 += entry.getKey() * entry.getKey();
                avgX += entry.getKey();
                avgY += entry.getValue();
            }

            return cache =
                    new AvgX2XY(avgX2 / map.size(), avgX / map.size(), avgY / map.size());
        }
    }

    @Override
    public String equationAsString(TreeMap<Double, Double> dataPoints) {
        return equation;
    }

    @Override
    public int getMinEntries() {
        return 3;
    }
}
