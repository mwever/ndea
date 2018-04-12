package ndea.ext.factory;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;

import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.meta.MultiClassClassifier;

public class TransformationClassifierFactory extends AbstractEnsembleClassifierFactory {

  public enum TypeOfTransformation {
    OvO, OvR;
  }

  private TypeOfTransformation type;

  public TransformationClassifierFactory(final TypeOfTransformation type) {
    super(new Random());
    this.type = type;
  }

  @Override
  public Classifier newInstance() throws Exception {
    if ((this.getSizeOfEnsemble() > 1) || (this.getNodeType() != EMCNodeType.DIRECT)) {
      return null;
    }

    switch (this.type) {
      case OvO:
        MultiClassClassifier allPairs = new MultiClassClassifier();
        allPairs.setClassifier(AbstractClassifier.forName(this.getBaseClassifier(), null));
        allPairs.setOptions(new String[] { "-M", "" + 3 });
        return allPairs;
      case OvR:
        MultiClassClassifier oneVSRest = new MultiClassClassifier();
        oneVSRest.setClassifier(AbstractClassifier.forName(this.getBaseClassifier(), null));
        return oneVSRest;
    }

    return null;
  }

}
