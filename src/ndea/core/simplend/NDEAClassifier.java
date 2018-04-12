package ndea.core.simplend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.moeaframework.core.PRNG;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;
import jaicore.ml.classification.multiclass.reduction.MCTreeNode;
import ndea.core.simplend.genotype.GenotypeRepresentation;
import ndea.core.simplend.objective.ITreeClassifierObjective;
import ndea.core.simplend.objective.MCCVTrimmedMeanError;
import ndea.core.simplend.objective.TestsetError;
import ndea.core.simplend.objective.VectorLength;
import ndea.core.simplend.util.chunk.MCCEAConfiguration;
import ndea.ext.IMCCVTestDataObserver;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class NDEAClassifier implements Classifier, Serializable, IMCCVTestDataObserver {

	/**
	 *
	 */
	private static final long serialVersionUID = -2650441083878552266L;
	private MCTreeNode polychotomy;
	private Random rand;
	private List<String> classifiers;
	private List<EMCNodeType> nodeTypes;

	private Instances testData;
	private File datasetFile;

	public NDEAClassifier(final Random rand, final List<String> classifierList) {
		this.rand = rand;
		this.classifiers = classifierList;
		this.nodeTypes = new LinkedList<>();
		this.nodeTypes.add(EMCNodeType.DIRECT);
	}

	@Override
	public void buildClassifier(final Instances data) throws Exception {
		MCCEAConfiguration config = new MCCEAConfiguration(1, 0.7, this.classifiers, this.rand.nextLong(),
				GenotypeRepresentation.DISTANCES, this.datasetFile, this.nodeTypes, new Long(100000));

		GenotypeRepresentation representation = config.getRepresentation();

		// setup random objects
		PRNG.setRandom(this.rand);
		try {
			// setup heuristic objectives
			List<ITreeClassifierObjective> objectiveList = new LinkedList<>();
			objectiveList.add(new MCCVTrimmedMeanError(data, 5, 0.9, this.rand));

			Map<String, ITreeClassifierObjective> attributeEvaluations = new HashMap<>();
			attributeEvaluations.put("selectionValue", new VectorLength(objectiveList.size()));
			if (testData != null) {
				attributeEvaluations.put("testError", new TestsetError(data, testData));
			}

			// clear caches (should not be necessary here, but just leave it here for
			// completeness)
			objectiveList.forEach(x -> x.clearCache());
			attributeEvaluations.values().stream().forEach(x -> x.clearCache());

			// instantiate and run algorithm
			MultiClassClassificationEA mccAlgorithm = new MultiClassClassificationEA(data.numClasses(), objectiveList,
					config.getNodeTypeList(), config.getClassifiers(), representation, this.rand,
					config.getSoftTimeout(), attributeEvaluations, 0.0, false);
			mccAlgorithm.run();

			this.polychotomy = mccAlgorithm.getSingleResult();
			this.polychotomy.buildClassifier(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void testData(final Instances data) {
		this.testData = data;
	}

	@Override
	public double classifyInstance(final Instance instance) throws Exception {
		return this.polychotomy.classifyInstance(instance);
	}

	@Override
	public double[] distributionForInstance(final Instance instance) throws Exception {
		return this.polychotomy.distributionForInstance(instance);
	}

	@Override
	public Capabilities getCapabilities() {
		return null;
	}

}
