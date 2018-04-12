package ndea.core.simplend.objective;

import jaicore.ml.classification.multiclass.reduction.MCTreeNode;

import org.moeaframework.core.Solution;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class TestsetError implements ITreeClassifierObjective {

  private Instances trainData;
  private Instances testData;

  public TestsetError(final Instances trainData, final Instances testData) {
    this.trainData = trainData;
    this.testData = testData;
  }

  @Override
  public double evaluate(final MCTreeNode individualPhenotype, final Solution solution) throws Exception {
    individualPhenotype.buildClassifier(this.trainData);

    Evaluation eval = new Evaluation(this.trainData);
    if (this.testData == null) {
      System.out.println(this);
      System.out.println(this.testData);
    }
    eval.evaluateModel(individualPhenotype, this.testData, new Object[] {});
    double errorRate = 1.0 - (eval.pctCorrect() / 100);

    return errorRate;
  }

  @Override
  public String toString() {
    return "TestsetError";
  }

  @Override
  public String getName() {
    return "testError";
  }

  @Override
  public double getWorstCaseValue() {
    return 1;
  }

  @Override
  public void clearCache() {

  }
}
