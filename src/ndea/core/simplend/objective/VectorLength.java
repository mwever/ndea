package ndea.core.simplend.objective;

import jaicore.ml.classification.multiclass.reduction.MCTreeNode;

import org.moeaframework.core.Solution;

public class VectorLength implements ITreeClassifierObjective {

  private int firstNObjectives;

  public VectorLength(final int firstNObjectives) {
    this.firstNObjectives = firstNObjectives;
  }

  @Override
  public double evaluate(final MCTreeNode individualPhenotype, final Solution solution) throws Exception {
    double sumOfSquares = 0;

    for (int i = 0; i < this.firstNObjectives; i++) {
      sumOfSquares += Math.pow(solution.getObjective(i), 2);
    }

    double sqrt = Math.sqrt(sumOfSquares);
    return sqrt;
  }

  @Override
  public String getName() {
    return "VecLength";
  }

  @Override
  public double getWorstCaseValue() {
    return Math.sqrt(this.firstNObjectives);
  }

  @Override
  public void clearCache() {
    // TODO Auto-generated method stub
    return;
  }

}
