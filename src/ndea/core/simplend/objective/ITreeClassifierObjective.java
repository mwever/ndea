package ndea.core.simplend.objective;

import jaicore.ml.classification.multiclass.reduction.MCTreeNode;

import org.moeaframework.core.Solution;

public interface ITreeClassifierObjective {

  public double evaluate(MCTreeNode individualPhenotype, Solution solution) throws Exception;

  public String getName();

  public double getWorstCaseValue();

  public void clearCache();

}
