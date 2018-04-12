package ndea.core.simplend;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.InjectedInitialization;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;
import jaicore.ml.classification.multiclass.reduction.MCTreeNode;
import ndea.core.simplend.genotype.GenotypeRepresentation;
import ndea.core.simplend.objective.ITreeClassifierObjective;
import ndea.core.simplend.phenotype.multiclasstree.MCTreeUtil;
import ndea.core.simplend.phenotype.multiclasstree.ParsedMCTreeSolution;
import ndea.core.simplend.util.DistanceConfig;
import ndea.core.simplend.util.NestedDichotomyGraph;

public class MCTreeProblem extends AbstractProblem {

	private AtomicInteger evalIDCoutner = new AtomicInteger(0);

	private int numberOfClasses;
	private List<ITreeClassifierObjective> objectiveList;
	private GenotypeRepresentation representation;
	private List<EMCNodeType> nodeTypeList;
	private List<String> classifierList;
	private AtomicInteger idCounter = new AtomicInteger(0);

	private Map<String, ITreeClassifierObjective> attributeEvaluations;

	public MCTreeProblem(final int numberOfClasses, final List<ITreeClassifierObjective> objectiveList,
			final List<EMCNodeType> nodeTypeList, final List<String> classifierList,
			final GenotypeRepresentation representation, final Random rand,
			final Map<String, ITreeClassifierObjective> attributeEvaluations) {
		super(NestedDichotomyGraph.numberOfAllPairwiseWeights(numberOfClasses) + (numberOfClasses - 1)
				+ ((classifierList.size() == 1) ? 0 : 1) * (numberOfClasses - 1), objectiveList.size());
		this.representation = representation;
		this.numberOfClasses = numberOfClasses;
		this.objectiveList = objectiveList;
		this.classifierList = classifierList;
		this.nodeTypeList = nodeTypeList;
		this.attributeEvaluations = attributeEvaluations;
	}

	@Override
	public void evaluate(final Solution solution) {
		if (solution.getAttribute("evalID") != null) {
			return;
		}
		solution.setAttribute("evalID", this.evalIDCoutner.incrementAndGet());
		ParsedMCTreeSolution parsedSolution = MCTreeUtil.parseFromSolution(solution, this.numberOfClasses,
				this.nodeTypeList, this.classifierList, this.representation);
		MCTreeNode classifier;
		try {
			classifier = MCTreeUtil.buildMCTree(parsedSolution.weights, parsedSolution.nodeTypes,
					parsedSolution.classifiers, this.nodeTypeList, this.classifierList);
			solution.setAttribute("cache.phenotype", classifier);

			for (ITreeClassifierObjective obj : this.objectiveList) {
				solution.setObjective(this.objectiveList.indexOf(obj), obj.evaluate(classifier, solution));
			}

			for (String attributeKey : this.attributeEvaluations.keySet()) {
				solution.setAttribute(attributeKey,
						this.attributeEvaluations.get(attributeKey).evaluate(classifier, solution));
			}

		} catch (Exception e) {
			e.printStackTrace();
			for (ITreeClassifierObjective obj : this.objectiveList) {
				solution.setObjective(this.objectiveList.indexOf(obj), Double.MAX_VALUE);
			}
		}

		// System.out.println(SolutionUtil.solutionToString(solution));
	}

	@Override
	public Solution newSolution() {
		int numberOfClassGenes = this.getNumberOfClassGenes();
		int numberOfClassifiers = (this.classifierList.size() > 1 ? this.numberOfClasses - 1 : 0);
		int numberOfNodeTypes = (this.nodeTypeList.size() > 1 ? this.numberOfClasses - 1 : 0);

		// calculate the number of needed variables and create a new solution
		int numberOfVariables = numberOfClassGenes + numberOfNodeTypes + numberOfClassifiers;

		Solution newSolution = new Solution(numberOfVariables, this.getNumberOfObjectives());
		newSolution.setAttribute("ID", this.idCounter.incrementAndGet());

		// set variables for the distances / coordinates of the classes
		this.assignClassGenes(newSolution, numberOfClassGenes);

		// set variables for the definition of the tree nodes: merge, direct
		this.assignNodeType(newSolution, numberOfClassGenes, numberOfNodeTypes, null);

		// set variable for the definition of which classifiers to use
		this.assignClassifiers(newSolution, numberOfClassGenes, numberOfNodeTypes, numberOfVariables);

		return newSolution;
	}

	private void assignClassGenes(final Solution newSolution, final int numberOfClassGenes) {
		// set variables for weights of the fully connected weighted graph
		if (this.representation == GenotypeRepresentation.PERMUTATION) {
			newSolution.setVariable(0, EncodingUtils
					.newPermutation(NestedDichotomyGraph.numberOfAllPairwiseWeights(this.numberOfClasses)));
		} else {
			IntStream.range(0, numberOfClassGenes).forEach(x -> {
				newSolution.setVariable(x,
						EncodingUtils.newReal(DistanceConfig.WEIGHT_LOWER_BOUND, DistanceConfig.WEIGHT_UPPER_BOUND));
			});
		}
	}

	private int getNumberOfClassGenes() {
		int numberOfClassGenes;
		switch (this.representation) {
		case COORDINATES:
			numberOfClassGenes = this.numberOfClasses;
			break;
		default:
		case DISTANCES:
			numberOfClassGenes = NestedDichotomyGraph.numberOfAllPairwiseWeights(this.numberOfClasses);
			break;
		case PERMUTATION:
			numberOfClassGenes = 1;
			break;
		}
		return numberOfClassGenes;
	}

	private void assignClassifiers(final Solution newSolution, final int numberOfClassGenes,
			final int numberOfClassifiers, final int numberOfVariables) {
		if (this.classifierList.size() > 1) {
			IntStream.range(numberOfClassGenes + numberOfClassifiers, numberOfVariables).forEach(x -> {
				newSolution.setVariable(x, EncodingUtils.newInt(0, this.classifierList.size() - 1));
			});
		}
	}

	private void assignNodeType(final Solution newSolution, final int numberOfClassGenes, final int numberOfClassifiers,
			final EMCNodeType fixedType) {
		// set variables for the definition of the tree nodes: merge, OvO, OvA, or
		// direct
		if (this.nodeTypeList.size() > 1) {
			IntStream.range(numberOfClassGenes, numberOfClassGenes + numberOfClassifiers).forEach(x -> {
				if (fixedType == null) {
					newSolution.setVariable(x, EncodingUtils.newInt(0, this.nodeTypeList.size() - 1));
				} else {
					RealVariable var = EncodingUtils.newInt(0, this.nodeTypeList.size() - 1);
					var.setValue(this.nodeTypeList.indexOf(fixedType));
					newSolution.setVariable(x, var);
				}
			});
		}
	}

	public Initialization seededInit(final int populationSize) {
		List<Solution> seededPopulation = new LinkedList<>();

		for (int cID = 0; cID < this.classifierList.size(); cID++) {
			int numberOfClassGenes = this.getNumberOfClassGenes();
			int numberOfClassifiers = (this.classifierList.size() > 1 ? this.numberOfClasses - 1 : 0);
			int numberOfNodeTypes = (this.nodeTypeList.size() > 1 ? this.numberOfClasses - 1 : 0);

			// calculate the number of needed variables and create a new solution
			int numberOfVariables = numberOfClassGenes + numberOfNodeTypes + numberOfClassifiers;

			for (EMCNodeType rootNodeType : this.nodeTypeList) {
				if (rootNodeType == EMCNodeType.MERGE) {
					continue;
				}

				Solution newSolution = this.newSolution();

				this.assignClassGenes(newSolution, numberOfClassGenes);

				// assign all the variables the node type merge
				if (this.nodeTypeList.contains(EMCNodeType.MERGE) && numberOfClassifiers > 0) {
					this.assignNodeType(newSolution, numberOfClassGenes, numberOfClassifiers, EMCNodeType.MERGE);
					((RealVariable) newSolution.getVariable(numberOfClassGenes + numberOfClassifiers - 1))
							.setValue(this.nodeTypeList.indexOf(rootNodeType));
				}

				if (numberOfClassifiers > 0) {
					this.assignClassifiers(newSolution, numberOfClassGenes, numberOfClassifiers, numberOfVariables);
					((RealVariable) newSolution.getVariable(numberOfVariables - 1)).setValue(cID);
				}

				seededPopulation.add(newSolution);
			}
		}

		Initialization init = new InjectedInitialization(this, populationSize, seededPopulation);
		return init;
	}

}
