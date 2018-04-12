package ndea.ext.baggedensemble;

import ndea.ext.factory.IClassifierFactory;
import weka.classifiers.Classifier;

public class EnsembleFactory implements IClassifierFactory {

  private final IClassifierFactory factory;
  private final int ensembleSize;

  public EnsembleFactory(final int ensembleSize, final IClassifierFactory iClassFac) {
    this.ensembleSize = ensembleSize;
    this.factory = iClassFac;
  }

  @Override
  public Classifier newInstance() throws Exception {
    Ensemble ensemble = new Ensemble();
    for (int ensembleMember = 0; ensembleMember < this.ensembleSize; ensembleMember++) {
      ensemble.add(this.factory.newInstance());
    }
    return ensemble;
  }

}
