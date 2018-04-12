package ndea.core.simplend.util;

import jaicore.basic.SetUtil.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ndea.core.simplend.util.graph.Graph;
import ndea.core.simplend.util.graph.GraphNode;
import ndea.core.simplend.util.graph.WeightedGraphEdge;

public class NestedDichotomyGraph extends Graph<GraphNode<Integer>, WeightedGraphEdge<GraphNode<Integer>>> {

  public NestedDichotomyGraph(final int numberOfClasses, final double[] distances) {
    Map<Integer, GraphNode<Integer>> graphNodeMap = new HashMap<>();
    IntStream.range(0, numberOfClasses).mapToObj(x -> new GraphNode<>(x)).forEach(x -> graphNodeMap.put(x.getValue(), x));
    this.addAllNodes(graphNodeMap.values());

    int index = 0;
    for (int i = 0; i < numberOfClasses - 1; i++) {
      for (int j = i + 1; j < numberOfClasses; j++) {
        this.addEdge(new WeightedGraphEdge<>(graphNodeMap.get(i), graphNodeMap.get(j), distances[index]));
        index++;
      }
    }
  }

  public NestedDichotomyGraph(final int numberOfClasses, final double[][] distanceMatrix) {
    Map<Integer, GraphNode<Integer>> graphNodeMap = new HashMap<>();
    IntStream.range(0, numberOfClasses).mapToObj(x -> new GraphNode<>(x)).forEach(x -> graphNodeMap.put(x.getValue(), x));
    this.addAllNodes(graphNodeMap.values());

    for (int i = 0; i < numberOfClasses - 1; i++) {
      for (int j = i + 1; j < numberOfClasses; j++) {
        this.addEdge(new WeightedGraphEdge<>(graphNodeMap.get(i), graphNodeMap.get(j), distanceMatrix[i][j]));
      }
    }
  }

  public GraphNode<Integer> getNode(final int value) {
    for (GraphNode<Integer> node : this.getNodes()) {
      if (node.getValue() == value) {
        return node;
      }
    }
    return null;
  }

  public List<Pair<Integer, Integer>> getSortedListOfDichotomyPairs() {
    List<WeightedGraphEdge<GraphNode<Integer>>> wgeList = new LinkedList<>(this.getEdges());
    Collections.sort(wgeList);
    return wgeList.stream().map(x -> new Pair<>(x.getA().getValue(), x.getB().getValue())).collect(Collectors.toList());
  }

  public static int numberOfAllPairwiseWeights(final int numberOfNodes) {
    int pairwiseWeights = numberOfNodes * (numberOfNodes - 1) / 2;
    return pairwiseWeights;
  }

  public static int numberOfNodes(final int numberOfAllPairwiseWeights) {
    int numberOfNodes = (int) Math.sqrt(numberOfAllPairwiseWeights * 2);
    return numberOfNodes;
  }

}
