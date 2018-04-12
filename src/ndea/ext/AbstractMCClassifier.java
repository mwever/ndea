package ndea.ext;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;
import jaicore.ml.classification.multiclass.reduction.MCTreeNode;

import java.io.Serializable;
import java.util.LinkedList;

import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;

public abstract class AbstractMCClassifier implements Classifier, Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 7346355467115376543L;

  protected MCTreeNode polychotomy;
  protected EMCNodeType nodeType = EMCNodeType.DIRECT;

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
    return new MCTreeNode(new LinkedList<>()).getCapabilities();
  }

  protected void setPolychotomy(final MCTreeNode polychotomy) {
    this.polychotomy = polychotomy;
  }

  public MCTreeNode getPolychotomy() {
    return this.polychotomy;
  }

  public void setNodeType(final EMCNodeType nodeType) {
    this.nodeType = nodeType;
  }
}
