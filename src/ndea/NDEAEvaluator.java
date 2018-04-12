package ndea;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ndea.core.simplend.NDEAClassifierFactory;
import ndea.ext.baggedensemble.EnsembleFactory;
import ndea.ext.factory.NDFactory;
import ndea.ext.factory.NDFactory.TypeOfND;
import ndea.ext.factory.TransformationClassifierFactory;
import ndea.ext.factory.TransformationClassifierFactory.TypeOfTransformation;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.END;
import weka.classifiers.meta.nestedDichotomies.ClassBalancedND;
import weka.classifiers.meta.nestedDichotomies.DataNearBalancedND;
import weka.classifiers.meta.nestedDichotomies.ND;
import weka.classifiers.meta.nestedDichotomies.RandomPairND;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class NDEAEvaluator {

	private static final long SEED = 1;

	private static Instances data;
	private static long seed;
	private static String currentBaseClassifier;

	/**
	 * 
	 * @param args
	 *            Array of arguments with [0]: path to the dataset
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		if(args.length<1) {
			System.out.println("A path to a dataset (arff format) file needs to be provided as an argument.");
			System.exit(0);
		}
		
		File datasetFile = new File(args[0]);
		data = new Instances(new FileReader(datasetFile));
		data.setClassIndex(data.numAttributes() - 1);

		List<String> classifiers = new LinkedList<>();
		classifiers.add(J48.class.getName());

		for (String baseClassifier : classifiers) {
			System.out.println(baseClassifier);
			/** SINGLE NDs */
			currentBaseClassifier = baseClassifier;
			List<String> baseClassifiers = new LinkedList<>();
			baseClassifiers.add(currentBaseClassifier);

			/** ENSEMBLES */

			TransformationClassifierFactory ovoFac = new TransformationClassifierFactory(TypeOfTransformation.OvO);
			ovoFac.setBaseClassifier(baseClassifier);
			evaluate("OvO-" + currentBaseClassifier, ovoFac.newInstance());

			TransformationClassifierFactory ovrFac = new TransformationClassifierFactory(TypeOfTransformation.OvR);
			ovrFac.setBaseClassifier(baseClassifier);
			evaluate("OvR-" + currentBaseClassifier, ovrFac.newInstance());

			// ND Ensemble
			END ndEnsemble = new END();
			ND nd = new ND();
			ndEnsemble.setClassifier(nd);
			evaluate("ND Ensemble", ndEnsemble);

			END rpndEnsemble = new END();
			RandomPairND rpnd = new RandomPairND();
			rpndEnsemble.setClassifier(rpnd);
			evaluate("RPND Ensemble", rpndEnsemble);

			END cbndEnsemble = new END();
			ClassBalancedND cbnd = new ClassBalancedND();
			cbndEnsemble.setClassifier(cbnd);
			evaluate("CBND Ensemble", cbndEnsemble);

			END dnbndEnsemble = new END();
			DataNearBalancedND dbnd = new DataNearBalancedND();
			dnbndEnsemble.setClassifier(dbnd);
			evaluate("DNBND Ensemble", dnbndEnsemble);

			// FCND Ensemble
			NDFactory fcndFac = new NDFactory(new Random(SEED), TypeOfND.FCND);
			fcndFac.setBaseClassifier(baseClassifier);
			EnsembleFactory fcndEfactory = new EnsembleFactory(10, fcndFac);
			evaluate("FCND Ensemble", fcndEfactory.newInstance());

			// NDEA Ensemble
			EnsembleFactory factory = new EnsembleFactory(10,
					new NDEAClassifierFactory(new Random(SEED), baseClassifiers));
			Classifier ensemble = factory.newInstance();
			evaluate("NDEA Ensemble", ensemble);

		}

	}

	private static void evaluate(final String name, final Classifier c) throws Exception {
		String accuracy = "";
		long startTime = System.currentTimeMillis();
		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(c, data, 10, new Random(seed), new Object[] {});
		accuracy = eval.pctCorrect() + "";
		StringBuilder sb = new StringBuilder();
		sb.append("candidate: " + name);
		sb.append(" baseClassifier: " + currentBaseClassifier);
		sb.append(" accuracy: " + accuracy);
		sb.append(" evalTime: " + (System.currentTimeMillis() - startTime) + "");
		System.out.println(sb.toString());

	}

}
