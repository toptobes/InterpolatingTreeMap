package very.bored.regressionfactories;

import very.bored.interpolatingtreemap.RegressionEquationFactory;

import java.util.TreeMap;
import java.util.function.Function;

public class QuadRegEquationFactory implements RegressionEquationFactory {

    public static void main(String[] args) {
        TreeMap<Double, Double> map = new TreeMap<>();
        map.put(1.0, 10.0);
        map.put(2.0, 5.0);
        map.put(3.0, 3.0);
        map.put(4.0, 5.0);
        map.put(5.0, 10.0);

        QuadRegEquationFactory factory = new QuadRegEquationFactory();
        Function<Double, Double> equation = factory.equationFrom(map);
    }

    @Override
    public Function<Double, Double> equationFrom(TreeMap<Double, Double> dataPoints) {
        double SSxx = calcSSxx(dataPoints);
        double SSxy = calcSSxy(dataPoints);
        double SSxx2 = calcSSxx2(dataPoints);
        double SSx2x2 = calcSSx2x2(dataPoints);
        double SSx2y = calcSSx2y(dataPoints);
        AvgX2XY means = AvgX2XY.from(dataPoints);

        double a = (SSx2y * SSxx - SSxy * SSxx2) / (SSxx * SSx2x2 - SSxx2 * SSxx2);
        double b = (SSxy * SSx2x2 - SSx2y * SSxx2) / (SSxx * SSx2x2 - SSxx2 * SSxx2);
        double c = means.y - b * means.x - a * means.x2;

        return x -> a * x * x + b * x + c;
    }

    private double calcSSxx(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSxx = 0.0;
        for (Double x : map.keySet()) {
            SSxx += (x - means.x) * (x - means.x);
        }

        return SSxx;
    }

    private double calcSSxy(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSxy = 0;
        for (var entry : map.entrySet()) {
            SSxy += (entry.getKey() - means.x) * (entry.getValue() - means.y);
        }

        return SSxy;
    }

    private double calcSSxx2(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSxx2 = 0;
        for (Double x : map.keySet()) {
            SSxx2 += (x - means.x) * (x * x - means.x2);
        }

        return SSxx2;
    }

    private double calcSSx2x2(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSx2x2 = 0;
        for (Double x : map.keySet()) {
            SSx2x2 += (x * x - means.x2) * (x * x - means.x2);
        }

        return SSx2x2;
    }

    private double calcSSx2y(TreeMap<Double, Double> map) {
        AvgX2XY means = AvgX2XY.from(map);

        double SSx2y = 0;
        for (var entry : map.entrySet()) {
            SSx2y += (entry.getKey() * entry.getKey() - means.x2) * (entry.getValue() - means.y);
        }

        return SSx2y;
    }

    private record AvgX2XY(double x2, double x, double y) {
        static AvgX2XY from(TreeMap<Double, Double> map) {
            double avgX2 = 0, avgX = 0, avgY = 0;
            for (var entry : map.entrySet()) {
                avgX2 += entry.getKey() * entry.getKey();
                avgX += entry.getKey();
                avgY += entry.getValue();
            }

            System.out.println("avgX2: " + avgX2 / map.size());
            System.out.println("avgX: " + avgX / map.size());
            System.out.println("avgY: " + avgY / map.size());

            return new AvgX2XY(avgX2 / map.size(), avgX / map.size(), avgY / map.size());
        }
    }
}
