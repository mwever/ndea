package ndea.core.simplend.phenotype.multiclasstree;

public class ParsedMCTreeSolution {
  public final double[] weights;
  public final int[] nodeTypes;
  public final int[] classifiers;

  public ParsedMCTreeSolution(final double[] weights, final int[] nodeTypes, final int[] classifiers) {
    this.weights = weights;
    this.nodeTypes = nodeTypes;
    this.classifiers = classifiers;
  }
}