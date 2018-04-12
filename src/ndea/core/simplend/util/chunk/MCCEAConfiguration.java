package ndea.core.simplend.util.chunk;

import jaicore.ml.classification.multiclass.reduction.EMCNodeType;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ndea.core.simplend.genotype.GenotypeRepresentation;
import ndea.core.simplend.util.portfolio.BaseClassifierPortfolio;
import ndea.core.simplend.util.portfolio.DatasetPortfolio;

public class MCCEAConfiguration implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 7563600831042649644L;
  private int taskID;
  private double trainingSplit;
  private List<String> classifiers;
  private long seed;
  private GenotypeRepresentation representation;
  private File dataset;
  private List<EMCNodeType> nodeTypeList;
  private long softTimeout;

  private MCCEAChunk chunk;

  public MCCEAConfiguration(final int taskID, final double trainingSplit, final List<String> classifiers, final long seed, final GenotypeRepresentation representation,
      final File dataset, final List<EMCNodeType> nodeTypeList, final long softTimeout) {
    this.taskID = taskID;
    this.trainingSplit = trainingSplit;
    this.classifiers = classifiers;
    this.seed = seed;
    this.representation = representation;
    this.dataset = dataset;
    this.nodeTypeList = nodeTypeList;
    this.softTimeout = softTimeout;
  }

  public MCCEAChunk getChunk() {
    return this.chunk;
  }

  public void setChunk(final MCCEAChunk chunk) {
    this.chunk = chunk;
  }

  public int getTaskID() {
    return this.taskID;
  }

  public double getTrainingSplit() {
    return this.trainingSplit;
  }

  public List<String> getClassifiers() {
    return this.classifiers;
  }

  public long getSeed() {
    return this.seed;
  }

  public File getDataset() {
    return this.dataset;
  }

  public GenotypeRepresentation getRepresentation() {
    return this.representation;
  }

  public static MCCEAConfiguration parseFrom(final String line) {
    String[] attributeSplit = line.split(";");

    List<EMCNodeType> nodeTypeList = new LinkedList<>();
    List<String> classifierList = new LinkedList<>();
    double trainingSplit = 0.6;
    long seed = 0;
    GenotypeRepresentation representation = GenotypeRepresentation.DISTANCES;
    File dataset = null;
    int taskID = -1;
    long softTimeout = -1;

    BaseClassifierPortfolio classifierPortfolio = new BaseClassifierPortfolio();
    DatasetPortfolio datasetPortfolio = new DatasetPortfolio();

    for (String attributeKV : attributeSplit) {
      String[] kvSplit = attributeKV.split("=");
      if (kvSplit.length != 2) {
        throw new IllegalArgumentException("Corrupted configuration chunk file. Cannot parse line " + line);
      }

      switch (kvSplit[0]) {
        case "taskID":
          taskID = Integer.parseInt(kvSplit[1]);
          break;
        case "nodetypes":
          String[] nodeTypeSplit = kvSplit[1].split(",");
          for (String nodeType : nodeTypeSplit) {
            nodeTypeList.add(EMCNodeType.valueOf(nodeType.trim().toUpperCase()));
          }
          break;
        case "classifiers":
          String[] classifierSplit = kvSplit[1].split(",");
          for (String classifier : classifierSplit) {
            classifierList.add(classifierPortfolio.simpleToFullName(classifier));
          }
          break;
        case "trainingsplit":
          trainingSplit = Double.parseDouble(kvSplit[1]);
          break;
        case "seed":
          seed = Long.parseLong(kvSplit[1]);
          break;
        case "representation":
          representation = GenotypeRepresentation.valueOf(kvSplit[1].toUpperCase());
          break;
        case "dataset":
          dataset = datasetPortfolio.get(kvSplit[1]);
          break;
        case "softTimeout":
          softTimeout = Long.parseLong(kvSplit[1]);
          break;
      }
    }

    return new MCCEAConfiguration(taskID, trainingSplit, classifierList, seed, representation, dataset, nodeTypeList, softTimeout);
  }

  public List<EMCNodeType> getNodeTypeList() {
    return this.nodeTypeList;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    return sb.toString();
  }

  public long getSoftTimeout() {
    return this.softTimeout;
  }

}
