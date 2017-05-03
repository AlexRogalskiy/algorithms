package org.briarheart.algorithms.graph;

import com.google.common.graph.Graph;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractGraphAlgorithm<T> {
    private Map<T, Integer> nodeIndexes = new HashMap<>();

    public AbstractGraphAlgorithm(Graph<T> graph) {
        checkNotNull(graph, "Graph cannot be null");
        for (T node : graph.nodes())
            if (!nodeIndexes.containsKey(node))
                nodeIndexes.put(node, nodeIndexes.size());
    }

    protected Integer indexOf(T node) {
        return nodeIndexes.get(node);
    }

    protected void checkNode(T node) {
        checkNotNull(node, "Node cannot be null");
        Integer index = indexOf(node);
        checkArgument(index != null, "Given node does not belong to this graph");
    }
}
