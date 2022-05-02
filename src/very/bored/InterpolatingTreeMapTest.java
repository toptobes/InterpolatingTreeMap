package very.bored;

import very.bored.interpolatingtreemap.InterpolatingTreeMap;
import very.bored.regressionfactories.LinRegEquationFactory;
import very.bored.regressionfactories.QuadRegEquationFactory;

import java.util.AbstractMap;
import java.util.Map;

@SuppressWarnings("unchecked")
class InterpolatingTreeMapTest {
    public static void main(String[] args) {
        var mapArray = new Map.Entry[]{
                new AbstractMap.SimpleEntry<>(1.0, 10.0),
                new AbstractMap.SimpleEntry<>(2.0, 5.0),
                new AbstractMap.SimpleEntry<>(3.0, 3.0),
                new AbstractMap.SimpleEntry<>(4.0, 5.0),
                new AbstractMap.SimpleEntry<>(5.0, 10.0),
        };

        var interpolatingTreeMap = new InterpolatingTreeMap(
                new QuadRegEquationFactory(), mapArray
        );

        System.out.println(interpolatingTreeMap.getCoefficientOfDetermination());
    }
}
