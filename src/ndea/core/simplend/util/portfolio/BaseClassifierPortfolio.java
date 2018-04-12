package ndea.core.simplend.util.portfolio;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SGD;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.functions.VotedPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class BaseClassifierPortfolio {

  public Map<Integer, String> classifierPortfolio;
  public Map<String, Integer> simpleNameToIndex;
  private int nextIndex = 0;

  public BaseClassifierPortfolio() {
    this.classifierPortfolio = new HashMap<>();
    this.simpleNameToIndex = new HashMap<>();

    Classifier[] portfolio = { new BayesNet(), new NaiveBayes(), new NaiveBayesMultinomial(), new GaussianProcesses(), new LinearRegression(), new Logistic(),
        new MultilayerPerceptron(), new SGD(), new SMO(), new SimpleLinearRegression(), new SimpleLogistic(), new VotedPerceptron(), new IBk(), new KStar(), new DecisionTable(),
        new JRip(), new M5Rules(), new OneR(), new PART(), new ZeroR(), new DecisionStump(), new J48(), new LMT(), new M5P(), new RandomForest(), new RandomTree(), new REPTree() };
    Arrays.stream(portfolio).forEach(this::add);
  }

  public void add(final Classifier name) {
    this.classifierPortfolio.put(this.nextIndex, name.getClass().getName());
    this.simpleNameToIndex.put(name.getClass().getSimpleName(), this.nextIndex);
    this.nextIndex++;
  }

  public List<String> wholePortfolio() {
    return this.classifierPortfolio.values().stream().collect(Collectors.toList());
  }

  public List<List<String>> get(final int[][] indexArray) {
    List<List<String>> classifierList = new LinkedList<>();
    for (int[] ensemble : indexArray) {
      List<String> ensembleList = new LinkedList<>();
      for (int index : ensemble) {
        ensembleList.add(this.classifierPortfolio.get(index));
      }
      classifierList.add(ensembleList);
    }
    return classifierList;
  }

  public List<List<String>> get(final String[][] simpleNameIndexArray) {
    List<List<String>> classifierList = new LinkedList<>();
    for (String[] ensemble : simpleNameIndexArray) {
      List<String> ensembleList = new LinkedList<>();
      for (String index : ensemble) {
        ensembleList.add(this.classifierPortfolio.get(this.simpleNameToIndex.get(index)));
      }
      classifierList.add(ensembleList);
    }
    return classifierList;
  }

  public String simpleToFullName(final String simpleName) {
    return this.classifierPortfolio.get(this.simpleNameToIndex.get(simpleName));
  }

}
