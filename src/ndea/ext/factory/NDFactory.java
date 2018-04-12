package ndea.ext.factory;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;

import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.meta.END;
import weka.classifiers.meta.nestedDichotomies.FurthestCentroidND;
import weka.classifiers.meta.nestedDichotomies.ND;
import weka.classifiers.meta.nestedDichotomies.RandomPairND;

public class NDFactory extends AbstractEnsembleClassifierFactory {

  public enum TypeOfND {
    ND, RPND, FCND;
  }

  private final TypeOfND type;

  public NDFactory(final Random rand, final TypeOfND type) {
    super(rand);
    this.type = type;
  }

  @Override
  public Classifier newInstance() throws Exception {
    if (this.getNodeType() != EMCNodeType.DIRECT) {
      return null;
    }

    assert this.getBaseClassifier() != null : "Base classifier must not be null";
    assert this.getSizeOfEnsemble() != 0 : "Need to be at least one classifier";

    switch (this.type) {
      case ND:
        if (this.getSizeOfEnsemble() == 1) {
          ND nestedDichotomy = new ND();
          nestedDichotomy.setClassifier(AbstractClassifier.forName(this.getBaseClassifier(), null));
          nestedDichotomy.setSeed(this.getRand().nextInt());
          return nestedDichotomy;
        } else {
          END ensemble = new END();
          ND nestedDichotomy = new ND();
          nestedDichotomy.setClassifier(AbstractClassifier.forName(this.getBaseClassifier(), null));
          ensemble.setClassifier(nestedDichotomy);
          ensemble.setSeed(this.getRand().nextInt());
          if (this.getSizeOfEnsemble() != 10) {
            ensemble.setNumIterations(this.getSizeOfEnsemble());
          }
          return ensemble;
        }
      case RPND:
        if (this.getSizeOfEnsemble() == 1) {
          RandomPairND rpnd = new RandomPairND();
          rpnd.setClassifier(AbstractClassifier.forName(this.getBaseClassifier(), null));
          rpnd.setSeed(this.getRand().nextInt());
          return rpnd;
        } else {
          END ensemble = new END();
          RandomPairND rpnd = new RandomPairND();
          rpnd.setClassifier(AbstractClassifier.forName(this.getBaseClassifier(), null));
          ensemble.setClassifier(rpnd);
          ensemble.setSeed(this.getRand().nextInt());
          if (this.getSizeOfEnsemble() != 10) {
            ensemble.setNumIterations(this.getSizeOfEnsemble());
          }
          return ensemble;
        }
      case FCND:
        if (this.getSizeOfEnsemble() == 1) {
          FurthestCentroidND fcnd = new FurthestCentroidND();
          fcnd.setClassifier(AbstractClassifier.forName(this.getBaseClassifier(), null));
          fcnd.setSeed(this.getRand().nextInt());
          return fcnd;
        }
    }
    return null;
  }

}
