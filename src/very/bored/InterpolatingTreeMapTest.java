package very.bored;

import very.bored.interpolatingtreemap.InterpolatingTreeMap;
import very.bored.regressionfactories.LinRegEquationFactory;

import java.util.AbstractMap;
import java.util.Map;

@SuppressWarnings("unchecked")
class InterpolatingTreeMapTest {
    public static void main(String[] args) {
        final var entries = new Map.Entry[]{
                new AbstractMap.SimpleEntry<>(1., 1.),
                new AbstractMap.SimpleEntry<>(3., 2.),
                new AbstractMap.SimpleEntry<>(5., 3.),
                new AbstractMap.SimpleEntry<>(7., 2.)
        };

        InterpolatingTreeMap map = new InterpolatingTreeMap.Builder()
                .regressionEquationFactory(new LinRegEquationFactory())
                .dataPoints(entries)
                .build();

        System.out.println("(" + -1.0 + ",  " + map.get(-1.0) + ")");
    }
}
