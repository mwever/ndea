package ndea.ext.factory;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;

import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;

public class ClassifierFactory extends AbstractEnsembleClassifierFactory {

  public ClassifierFactory(final Random rand) {
    super(rand);
  }

  @Override
  public Classifier newInstance() throws Exception {
    if (this.getSizeOfEnsemble() != 1 || this.getNodeType() != EMCNodeType.DIRECT) {
      return null;
    }

    return AbstractClassifier.forName(this.getBaseClassifier(), null);
  }

}
