package ndea.core.simplend;

import java.util.List;
import java.util.Random;

import ndea.ext.factory.IClassifierFactory;
import weka.classifiers.Classifier;

public class NDEAClassifierFactory implements IClassifierFactory {


  private Random rand;
  private List<String> classifierList;

  public NDEAClassifierFactory(final Random rand, final List<String> classifierList) {
    this.rand = rand;
    this.classifierList = classifierList;
  }

  @Override
  public Classifier newInstance() throws Exception {
        return new NDEAClassifier(new Random(this.rand.nextLong()), this.classifierList);
  }

}
