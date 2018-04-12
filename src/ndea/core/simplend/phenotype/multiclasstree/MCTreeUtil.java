package ndea.core.simplend.phenotype.multiclasstree;

import jaicore.basic.SetUtil.Pair;
import jaicore.ml.classification.multiclass.reduction.EMCNodeType;
import jaicore.ml.classification.multiclass.reduction.MCTreeNode;
import jaicore.ml.classification.multiclass.reduction.MCTreeNodeLeaf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

import ndea.core.simplend.genotype.GenotypeRepresentation;
import ndea.core.simplend.util.NestedDichotomyGraph;

public class MCTreeUtil {

  public static MCTreeNode buildMCTree(final double[] distances, final int[] nodeTypes, final int[] classifiers, final List<EMCNodeType> nodeTypeList,
      final List<String> classifierList) throws Exception {
    NestedDichotomyGraph graph = new NestedDichotomyGraph(classifiers.length + 1, distances);

    Map<Integer, MCTreeNode> nodeMap = new HashMap<>();

    int nodeIndex = 0;
    MCTreeNode parentNode = null;
    for (Pair<Integer, Integer> nodePair : graph.getSortedListOfDichotomyPairs()) {
      MCTreeNode left = null;
      MCTreeNode right = null;
      if (nodeMap.containsKey(nodePair.getX())) {
        left = nodeMap.get(nodePair.getX());
        if (left.getContainedClasses().contains(nodePair.getY())) {
          continue;
        }
      } else {
        left = new MCTreeNodeLeaf(nodePair.getX());
      }
      if (nodeMap.containsKey(nodePair.getY())) {
        right = nodeMap.get(nodePair.getY());
      } else {
        right = new MCTreeNodeLeaf(nodePair.getY());
      }

      EMCNodeType nodeType = nodeTypeList.get(nodeTypes[nodeIndex]);
      if (nodeIndex == nodeTypes.length - 1 && nodeType == EMCNodeType.MERGE) {
        nodeType = EMCNodeType.DIRECT;
      }

      List<Integer> containedClasses = new LinkedList<>(left.getContainedClasses());
      containedClasses.addAll(right.getContainedClasses());

      parentNode = new MCTreeNode(containedClasses, nodeType, classifierList.get(classifiers[nodeIndex]));

      parentNode.addChild(left);
      parentNode.addChild(right);

      for (Integer leftClasses : left.getContainedClasses()) {
        nodeMap.put(leftClasses, parentNode);
      }
      for (Integer rightClasses : right.getContainedClasses()) {
        nodeMap.put(rightClasses, parentNode);
      }
      nodeIndex++;
    }

    return parentNode;
  }

  public static ParsedMCTreeSolution parseFromSolution(final Solution solution, final int numberOfClasses, final List<EMCNodeType> nodeTypeList, final List<String> classifierList,
      final GenotypeRepresentation representation) {
    double[] weights = new double[NestedDichotomyGraph.numberOfAllPairwiseWeights(numberOfClasses)];
    int[] nodeTypes = new int[numberOfClasses - 1];
    int[] classifiers = new int[numberOfClasses - 1];
    final int representationLength;

    int numberOfNodeTypes = (nodeTypeList.size() > 1) ? numberOfClasses - 1 : 0;
    int numberOfClassifiers = (classifierList.size() > 1) ? numberOfClasses - 1 : 0;

    switch (representation) {
      case COORDINATES:
        double[] coordinates = new double[numberOfClasses];
        IntStream.range(0, coordinates.length).forEach(x -> {
          coordinates[x] = EncodingUtils.getReal(solution.getVariable(x));
        });

        int index = 0;
        for (int i = 0; i < coordinates.length - 1; i++) {
          for (int j = i + 1; j < coordinates.length; j++) {
            double distanceBetweenIAndJ = Math.abs(coordinates[i] - coordinates[j]);
            weights[index++] = distanceBetweenIAndJ;
          }
        }
        representationLength = coordinates.length;
        break;
      default:
      case DISTANCES:
        IntStream.range(0, weights.length).forEach(x -> {
          weights[x] = EncodingUtils.getReal(solution.getVariable(x));
        });
        representationLength = weights.length;
        break;
      case PERMUTATION:
        int[] permutation = EncodingUtils.getPermutation(solution.getVariable(0));
        IntStream.range(0, permutation.length).forEach(x -> {
          weights[x] = permutation[x];
        });
        representationLength = 1;
        break;
    }

    if (nodeTypeList.size() > 1) {
      IntStream.range(representationLength, representationLength + numberOfNodeTypes).forEach(x -> {
        nodeTypes[x % representationLength] = EncodingUtils.getInt(solution.getVariable(x));
      });
    } else {
      IntStream.range(0, nodeTypes.length).forEach(x -> {
        nodeTypes[x] = 0;
      });
    }

    if (classifierList.size() > 1) {
      IntStream.range(representationLength + numberOfNodeTypes, representationLength + numberOfNodeTypes + numberOfClassifiers).forEach(x -> {
        classifiers[x % (representationLength + numberOfNodeTypes)] = EncodingUtils.getInt(solution.getVariable(x));
      });
    } else {
      IntStream.range(0, classifiers.length).forEach(x -> {
        classifiers[x] = 0;
      });
    }

    return new ParsedMCTreeSolution(weights, nodeTypes, classifiers);
  }

}
