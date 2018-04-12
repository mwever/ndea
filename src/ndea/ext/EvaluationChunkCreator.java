package ndea.ext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.analysis.function.Logistic;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;

public class EvaluationChunkCreator {

  private static final int NUMBER_OF_SEEDS = 1;
  private static final long REMAINING_TIME = 60 * 60 * 3;

  public static void main(final String[] args) {

    // String[] datasets = new DatasetPortfolio().allSimpleNames().toArray(new String[] {});
    // String[] datasets = { "audiology", "letter", "kropt", "mfeat-fourier", "mfeat-factors",
    // "mfeat-karhunen", "mfeat-pixel", "optdigits", "page-blocks", "pendigits", "segment",
    // "vowel", "yeast", "zoo" };
    // String[] datasets = { "mfeat-pixel", "waveform5000" };

    String[] datasets = { "audiology", "balance.scale", "letter", "kropt", "mfeat-fourier", "mfeat-factors", "mfeat-karhunen", "mfeat-pixel", "optdigits", "page-blocks",
        "pendigits", "segment", "vowel", "waveform5000", "yeast", "zoo" };
    String[] candidates = { "AP", "OVR", "ND", "RPND", "RSNP", "RRSNP", "RPRND", "BinRSNP", "BC" };

    String[] classifiers = { J48.class.getName(), Logistic.class.getName(), RandomForest.class.getName(), DecisionStump.class.getName(), MultilayerPerceptron.class.getName() };
    String[] nodeTypes = { "direct" };
    String[] representations = { "distances" };
    Set<Long> seeds = new HashSet<>();
    double[] trainingSplits = { 0.6 };

    Random r = new Random(12345);

    while (seeds.size() < NUMBER_OF_SEEDS) {
      seeds.add(r.nextLong());
    }

    AtomicInteger taskID = new AtomicInteger(1);

    StringBuilder chunkString = new StringBuilder();
    int chunkID = (int) (System.currentTimeMillis() / 1000);
    chunkString.append("chunkID=" + chunkID + "\n");

    Arrays.stream(datasets).forEach(dataset -> {
      Arrays.stream(trainingSplits).forEach(split -> {
        seeds.stream().forEach(seed -> {
          Arrays.stream(classifiers).forEach(classifierList -> {
            Arrays.stream(nodeTypes).forEach(nodeTypeList -> {
              Arrays.stream(representations).forEach(representation -> {
                int currentTaskID = taskID.getAndIncrement();
                chunkString.append("taskID=" + currentTaskID);
                chunkString.append(";seed=" + seed);
                chunkString.append(";nodetypes=" + nodeTypeList);
                chunkString.append(";classifiers=" + classifierList);
                chunkString.append(";representation=" + representation);
                chunkString.append(";dataset=" + dataset);
                chunkString.append(";trainingsplit=" + split);
                chunkString.append(";softTimeout=" + REMAINING_TIME);
                chunkString.append("\n");
              });
            });
          });
        });
      });
    });

    System.out.println("Tasks added: " + (taskID.get() - 1));
    File chunkOutputFile = new File(chunkID + ".chunk");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(chunkOutputFile))) {
      bw.write(chunkString.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
