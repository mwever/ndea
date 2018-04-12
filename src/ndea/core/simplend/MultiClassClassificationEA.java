package ndea.core.simplend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.GAVariation;
import org.moeaframework.core.operator.InjectedInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;

import com.google.common.collect.Streams;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;
import jaicore.ml.classification.multiclass.reduction.MCTreeNode;
import ndea.core.simplend.genotype.GenotypeRepresentation;
import ndea.core.simplend.objective.ITreeClassifierObjective;
import ndea.core.simplend.util.SolutionUtil;

public class MultiClassClassificationEA {
  private static int counter = 0;
  public static final boolean DEBUG = true;

  public static final int POPULATION_SIZE = 16;
  public static final int NUMBER_OF_GENERATIONS = 200;

  public static final int REINITIALIZE = 5;
  public static final int STORM = 0;
  public static final int STORM_SURVIVORS = POPULATION_SIZE / 10;

  public static final double EPSILON = 0.00001;
  public static final double NUMBER_OF_GENERATIONS_WO_IMPROVEMENT = 15;
  private int epsilonCounter = 0;
  private double lastBest = 1.0;

  private List<ITreeClassifierObjective> objectiveList;
  private List<String> classifierList;
  private List<EMCNodeType> nodeTypeList;
  private int numberOfClasses;
  private GenotypeRepresentation representation;


  private Problem mcTreeProblem;
  private Variation variation;
  private TournamentSelection selection;
  private NSGAII algorithm;

  private long softTimeout;

  private int numberOfEvolvedGenerations = 0;
  private boolean timeoutOccurred = false;
  private double baseline;
  private boolean verbose = true;

  private Map<String, ITreeClassifierObjective> attributeEvaluations;

  public MultiClassClassificationEA(final int numberOfClasses, final List<ITreeClassifierObjective> objectiveList, 
      final List<EMCNodeType> nodeTypeList, final List<String> classifierList, final GenotypeRepresentation representation,  final Random rand,
      final long softTimeout, final Map<String, ITreeClassifierObjective> attributeEvaluations, final double baseline, final boolean verbose)
      throws FileNotFoundException, IOException {
    this.objectiveList = objectiveList;
    this.classifierList = classifierList;
    this.nodeTypeList = nodeTypeList;
    this.numberOfClasses = numberOfClasses;
    this.softTimeout = softTimeout;
    this.baseline = baseline;
    this.representation = representation;
    this.verbose = verbose;

    this.attributeEvaluations = attributeEvaluations;

    // setup building blocks for NSGA-II
    this.mcTreeProblem = new MCTreeProblem(this.numberOfClasses, this.objectiveList,  this.nodeTypeList, this.classifierList, representation, rand,
        this.attributeEvaluations);
    // Initialization init = new RandomInitialization(this.mcTreeProblem, POPULATION_SIZE);
    Initialization init = ((MCTreeProblem) this.mcTreeProblem).seededInit(POPULATION_SIZE);
    this.selection = new TournamentSelection(2, new ChainedComparator(new ParetoDominanceComparator(), new CrowdingComparator()));
    this.variation = new GAVariation(new SBX(1, 25.0), new PM(1.0 / this.mcTreeProblem.getNumberOfVariables(), 30.0));
    // init algorithm
    this.algorithm = new NSGAII(this.mcTreeProblem, new NondominatedSortingPopulation(), null, this.selection, this.variation, init);

  }

  private boolean timedOut(final long startTime) {
    if (this.softTimeout <= 0) {
      return false;
    }
    long usedTime = (System.currentTimeMillis() - startTime) / 1000;
    long remainingTime = this.softTimeout - usedTime;

    return remainingTime < 0;
  }

  public void run() {
    long startTime = System.currentTimeMillis();

    for (int generation = 0; !this.timedOut(startTime) && generation < NUMBER_OF_GENERATIONS; generation++) {
      // System.out.println("Generation #" + generation + " Epsilon Counter " + this.epsilonCounter + " "
      // + this.classifierList);
      boolean reinit = false;
      if (REINITIALIZE > 0 && generation > 0 && generation % REINITIALIZE == 0) {
        this.reinitialize();
        reinit = true;
      }

      this.algorithm.step();
      this.numberOfEvolvedGenerations++;

      boolean improvement = false;
      for (Solution individual : this.algorithm.getResult()) {
        double solutionValue = (double) individual.getAttribute("selectionValue");
        if (solutionValue < this.lastBest - EPSILON) {
          this.lastBest = solutionValue;
          this.epsilonCounter = 0;
          improvement = true;
        }
      }

      if (!improvement) {
        this.epsilonCounter++;
        if (this.epsilonCounter > NUMBER_OF_GENERATIONS_WO_IMPROVEMENT) {
          break;
        }
      }

      if (this.attributeEvaluations.containsKey("testPerformance")) {
        double bestTestError = 1.0;
        bestTestError = Streams.stream(this.algorithm.getResult()).map(x -> (Double) x.getAttribute("testPerformance")).mapToDouble(x -> x).min().getAsDouble();
        System.out.println("Best Elite Test Performance of Generation #" + generation + ": " + bestTestError);
      }

      Solution bestSolution = null;
      for (Solution solution : this.algorithm.getResult()) {
        if (bestSolution == null || (double) solution.getAttribute("selectionValue") < (double) bestSolution.getAttribute("selectionValue")) {
          bestSolution = solution;
        }
      }

      if (this.verbose) {
        System.out.println(generation + " " + SolutionUtil.solutionToString(bestSolution));
      }
    }

    if (this.timedOut(startTime)) {
      this.timeoutOccurred = true;
    }
  }

  private void reinitialize() {
    NondominatedPopulation elit = this.algorithm.getResult();
    List<Solution> elitList = new LinkedList<>();
    for (Solution s : elit) {
      Solution solutionCopy = new Solution(s.getNumberOfVariables(), s.getNumberOfObjectives());
      for (int i = 0; i < s.getNumberOfVariables(); i++) {
        solutionCopy.setVariable(i, s.getVariable(i).copy());
      }
      elitList.add(s);
    }
    Initialization init = new InjectedInitialization(this.mcTreeProblem, POPULATION_SIZE, elitList);
    this.algorithm = new NSGAII(this.mcTreeProblem, new NondominatedSortingPopulation(), null, this.selection, this.variation, init);
  }

  public List<Solution> getResult() {
    List<Solution> resultList = new LinkedList<>();

    for (Solution individual : this.algorithm.getResult()) {
      resultList.add(individual);
    }

    return resultList;
  }

  public int getNumberOfEvolvedGenerations() {
    return this.numberOfEvolvedGenerations;
  }

  public boolean isTimedOut() {
    return this.timeoutOccurred;
  }

  public MCTreeNode getSingleResult() {
    Solution bestSolution = null;
    for (Solution solution : this.algorithm.getResult()) {
      if (bestSolution == null || (double) solution.getAttribute("selectionValue") < (double) bestSolution.getAttribute("selectionValue")) {
        bestSolution = solution;
      }
    }
    System.out.println((counter++) + "\t" + SolutionUtil.solutionToString(bestSolution));
    MCTreeNode polychotomy = (MCTreeNode) bestSolution.getAttribute("cache.phenotype");
    return polychotomy;
  }

}
