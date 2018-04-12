package ndea.core.simplend.objective;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.moeaframework.core.Solution;

import jaicore.ml.classification.multiclass.reduction.MCTreeNode;
import ndea.ext.MonteCarloCVEvaluation;
import weka.core.Instances;

public class MCCVTrimmedMeanError implements ITreeClassifierObjective {

  private final Instances data;
  private final int numRepetitions;
  private final double splitSize;
  private final Random rand;

  public MCCVTrimmedMeanError(final Instances data, final int numRepetitions, final double splitSize, final Random rand) {
    this.data = data;
    this.numRepetitions = numRepetitions;
    this.splitSize = splitSize;
    this.rand = rand;
  }

  @Override
  public double evaluate(final MCTreeNode individualPhenotype, final Solution solution) throws Exception {
    MonteCarloCVEvaluation mccvEval = new MonteCarloCVEvaluation(this.data, this.numRepetitions, this.splitSize, null);
    mccvEval.mcCrossValidateModel(individualPhenotype, this.data, this.rand);

    List<Double> pctCorrects = new LinkedList<>(mccvEval.getPctCorrectList());
    Collections.sort(pctCorrects);

    int removeElements = (int) (this.numRepetitions * 0.2);
    for (int i = 0; i < removeElements; i++) {
      pctCorrects.remove(0);
      pctCorrects.remove(pctCorrects.size() - 1);
    }

    return 1 - (pctCorrects.stream().mapToDouble(x -> x).average().getAsDouble());
    // return 1 - (mccvEval.minPctCorrect());
  }

  @Override
  public String getName() {
    return "MCCVMaxError";
  }

  @Override
  public double getWorstCaseValue() {
    return 1;
  }

  @Override
  public void clearCache() {
    return;
  }

}
