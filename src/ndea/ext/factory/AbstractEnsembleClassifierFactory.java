package ndea.ext.factory;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;

import java.util.Random;

public abstract class AbstractEnsembleClassifierFactory implements IClassifierFactory {

  private String baseClassifier;
  private int sizeOfEnsemble = 1;
  private Random rand;
  private EMCNodeType nodeType = EMCNodeType.DIRECT;

  protected AbstractEnsembleClassifierFactory(final Random rand, final String baseClassifier, final int sizeOfEnsemble) {
    this.rand = rand;
    this.sizeOfEnsemble = sizeOfEnsemble;
    this.baseClassifier = baseClassifier;
  }

  protected AbstractEnsembleClassifierFactory(final Random rand) {
    this.rand = rand;
  }

  public String getBaseClassifier() {
    return this.baseClassifier;
  }

  public void setBaseClassifier(final String baseClassifier) {
    this.baseClassifier = baseClassifier;
  }

  public EMCNodeType getNodeType() {
    return this.nodeType;
  }

  public void setNodeType(final EMCNodeType nodeType) {
    this.nodeType = nodeType;
  }

  public void setEnsembleSize(final int sizeOfEnsemble) {
    this.sizeOfEnsemble = sizeOfEnsemble;
  }

  public int getSizeOfEnsemble() {
    return this.sizeOfEnsemble;
  }

  public Random getRand() {
    return this.rand;
  }

  public void setRand(final Random rand) {
    this.rand = rand;
  }

  public void setRandomSeed(final long seed) {
    this.rand.setSeed(seed);
  }

}
