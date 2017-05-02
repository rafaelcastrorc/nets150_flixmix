import java.util.*;

/**
 * Created by rafaelcastro on 5/1/17.
 */
public class Graph <V> {

        private Map<V, HashMap<V, Optional<Double>>> map = new HashMap<>();
        private Map<V, Set<V>> inDegree = new HashMap<>();
        private Map<V, Set<V>> outDegree = new HashMap<>();

        public void addVertex(V vertex) {
            if (vertex == null)
                throw new IllegalArgumentException();
            if (map.containsKey(vertex))
                return;
            map.put(vertex, new HashMap<V, Optional<Double>>());
        }

        public void addEdge(V from, V to, Optional<Double> weight) {
            if (from == null || to == null)
                throw new IllegalArgumentException();
            addVertex(from);
            addVertex(to);
            if (!inDegree.containsKey(to)) {
                inDegree.put(to, new HashSet<>());
            }

            if (!outDegree.containsKey(from)) {
                outDegree.put(from, new HashSet<>());
            }

            inDegree.get(to).add(from);
            outDegree.get(from).add(to);

            map.get(from).put(to, weight);
            map.get(to).put(from, weight);

        }

        /// Actual methods
        public Optional<Double> getWeight(V u, V v) {
            if (u == null || v == null)
                throw new IllegalArgumentException();
            if (!map.containsKey(u) || !map.containsKey(v))
                throw new IllegalArgumentException();
            if (!hasDirectedEdge(u, v))
                return Optional.empty();
            HashMap<V, Optional<Double>> miniMap = map.get(u);
            Optional<Double> e = miniMap.get(v);
            if (e == null)
                throw new IllegalArgumentException();
            return e;

        }

        public Set<V> neighbors(V v) {
            return outNeighbors(v);
        }

        public Set<V> vertexSet() {
            return Collections.unmodifiableSet(map.keySet());
        }

        public Set<V> inNeighbors(V v) {
            if (v == null) {
                throw new IllegalArgumentException();
            }
            if (!map.containsKey(v)) {
                throw new IllegalArgumentException();
            }

            if (inDegree.get(v) == null) {
                return Collections.emptySet();
            }
            return Collections.unmodifiableSet(inDegree.get(v));
        }

        public Set<V> outNeighbors(V v) {
            if (v == null) {
                throw new IllegalArgumentException();
            }
            if (!map.containsKey(v))
                throw new IllegalArgumentException();

            if (outDegree.get(v) == null) {
                return Collections.emptySet();
            }
            return Collections.unmodifiableSet(outDegree.get(v));
        }

        public boolean hasDirectedEdge(V u, V v) {
            if (u == null || v == null)
                throw new IllegalArgumentException();
            if (!map.containsKey(u) || !map.containsKey(v))
                throw new IllegalArgumentException();

            HashMap<V, Optional<Double>> miniMap = map.get(u);
            return miniMap.containsKey(v);
        }


}

