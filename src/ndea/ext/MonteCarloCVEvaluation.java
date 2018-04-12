package ndea.ext;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jaicore.ml.WekaUtil;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

public class MonteCarloCVEvaluation {

  private final Instances data;
  private final int numRepetitions;
  private final double splitSize;
  private final IMCCVTestDataObserver observer;

  private List<Double> pctCorrectList;

  public MonteCarloCVEvaluation(final Instances data, final int numRepetitions, final double splitSize, final IMCCVTestDataObserver observer) {
    this.data = data;
    this.numRepetitions = numRepetitions;
    this.splitSize = splitSize;
    this.observer = observer;
  }

  public void mcCrossValidateModel(final Classifier classifier, final Instances data, final Random rand) {
    this.pctCorrectList = new LinkedList<>();
    for (int rep = 0; rep < this.numRepetitions; rep++) {
      List<Instances> stratifiedSplit = WekaUtil.getStratifiedSplit(data, rand, this.splitSize);

      if (this.observer != null) {
        this.observer.testData(stratifiedSplit.get(1));
      }

      try {
        classifier.buildClassifier(stratifiedSplit.get(0));
        Evaluation eval = new Evaluation(this.data);
        eval.evaluateModel(classifier, stratifiedSplit.get(1), new Object[] {});
        if (this.observer != null) {
          System.out.println(eval.pctCorrect());
        }
        this.pctCorrectList.add((eval.pctCorrect() / 100));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public double avgPctCorrect() {
    return StatisticsUtil.mean(this.pctCorrectList);
  }

  public double stdPctCorrect() {
    return StatisticsUtil.standardDeviation(this.pctCorrectList);
  }

  public double minPctCorrect() {
    return this.pctCorrectList.stream().mapToDouble(x -> x).min().getAsDouble();
  }

  public double maxPctCorrect() {
    return this.pctCorrectList.stream().mapToDouble(x -> x).max().getAsDouble();
  }

  public List<Double> getPctCorrectList() {
    return this.pctCorrectList;
  }
}
