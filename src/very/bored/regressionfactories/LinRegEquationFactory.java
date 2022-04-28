package very.bored.regressionfactories;

import very.bored.interpolatingtreemap.RegressionEquationFactory;

import java.util.TreeMap;
import java.util.function.Function;

public class LinRegEquationFactory implements RegressionEquationFactory {

    @Override
    public Function<Double, Double> equationFrom(TreeMap<Double, Double> dataPoints) {
        double SSxx = calcSSxx(dataPoints);
        double SSxy = calcSSxy(dataPoints);

        return x -> x * SSxy / SSxx + calcIntercept(SSxx, SSxy, dataPoints);
    }

    private double calcIntercept(double SSxx, double SSxy, TreeMap<Double, Double> map) {
        AvgXY means = AvgXY.from(map);
        return means.y - (SSxy * means.x / SSxx);
    }

    private double calcSSxx(TreeMap<Double, Double> map) {
        AvgXY means = AvgXY.from(map);

        double SSxx = 0.0;
        for (Double x : map.keySet()) {
            SSxx += (map.get(x) - means.y) * (map.get(x) - means.y);
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
}