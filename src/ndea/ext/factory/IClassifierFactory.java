package ndea.ext.factory;

import weka.classifiers.Classifier;

public interface IClassifierFactory {

  public Classifier newInstance() throws Exception;

}
